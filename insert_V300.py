import pymysql
import os

from utils.time_test import time_test


class Mysql:
    def __init__(self, user, password):
        self.user = user
        self.password = password
        self.database = "eventmanagement"
        self.conn = None
        self.cursor = None


    def connect_mysql_db(self):
        self.conn = pymysql.connect(host="127.0.0.1", port=3306,
                               user=self.user, password=self.password, charset="utf8")
        self.cursor = self.conn.cursor()
        self.cursor.execute(f'create database if not exists {self.database}')
        self.cursor.execute(f'use {self.database}')


    def create_table(self, input_titles):
        self.cursor.execute(f"create table if not exists trainevents({input_titles})")
        try:
            self.cursor.execute("ALTER TABLE trainevents ADD CONSTRAINT unique_values UNIQUE (`ProjectName`, `TrainsetNumber`, `TrainDevice`, `Mnemonic`, `OccurenceDate`, `TCodeName`, `LocationCode`)")
        except:
            print("unique data already satisfied")
        self.conn.commit()


    def insert_data(self, input_fields, input_values):
        # 获取字段数量, 并准备相应数量的占位符
        values_placeholder = ', '.join(['%s'] * len(input_fields.split(',')))
        sql = f"INSERT ignore INTO trainevents ({input_fields}) VALUES ({values_placeholder})"

        try:
            self.cursor.executemany(sql, input_values)
        except Exception as e:
            print(e)
            print("\n")


    def commit(self):
        self.conn.commit()


    def close_connect(self):
        self.conn.commit()
        self.cursor.close()
        self.conn.close()



"""
获取mysql字段 

Return: 
    str_field -> str:  得到插入字段值, 即 insert into demo(@return) values() 这部分  
"""
def get_field(first_line) -> str:
    str_field = ''
    # 加上文件名字
    str_field += '`ProjectName`, `TrainsetNumber`, `TrainDevice`, `SoftwareVersion`, '

    # 处理文件标题
    first_line = first_line.replace("\ufeff", "")
    first_line = first_line.replace('.', '_')
    list_title = first_line.split(';')
    list_title = [f"`{each}`" for each in list_title]
    str_field += ', '.join(list_title)
    str_field = str_field.strip()
    return str_field


"""
获取字段和文本内容 (数组形式)

Returns:
    getTitle -> str:  通过get_field函数获取要插入table的字段
    text_content -> list[list[str]]:  获取要插入的values值
"""
def get_field_values(input_path, input_file_name) -> (str, list[list[str]]):
    text_content = []
    with open(input_path, mode='r', encoding="UTF-8") as input_file:
        first_line = input_file.readline().strip()   # 第一行给get_title()使用

        end_of_line = ''   # end_of_line用于存储 ;;;;; 数据
        for line in input_file:
            line = line.replace("'", "o")    # '改成o, 防止误判
            line = line.strip()

            if line.startswith(';;;;;;;;;;'):
                line = line.replace("'", "").replace('"', "")   # '和"删除, 因为不是作为一个单独数据
                line = line.strip(';').strip()
                end = line.replace(';', ':')   # 只替换一个; 也就是第一个
                end += ','
                end_of_line += end
                continue

            # 正常的情况
            if not line.startswith(';;;;;;;;;;'):
                line = line.replace("\"", "'")  # "改成', 作为一个单独数据
                line = line.split(';')   # 由于最后有一个分号, 数据会多出来
                line.pop()     # 去除最后的数据

                # 如果不为空, 插入末尾的数据
                if end_of_line != '':
                    text_content[-1].append(repr(end_of_line))
                    end_of_line = ''

                # 插入文件名中的基础信息
                a, b, c, d, *_ = input_file_name.split("_")
                line = [repr(a), repr(b), repr(c), repr(d)] + line
                text_content.append(line)

        # 读取完文件将最后的end_of_line插入进text_content
        if end_of_line != '':
            text_content[-1].append(repr(end_of_line))
            end_of_line = ''

    field_ = get_field(first_line)
    return field_, text_content



@time_test
def main():
    paths = os.listdir("./unhandled")
    destination_folder = "./handled"

    # 获取所有文件路径, 并去除所有非csv文件
    paths = ["./unhandled/" + each for each in paths if each.split(".")[-1] == "csv"]

    mysql = Mysql("root", "root")
    mysql.connect_mysql_db()

    field_title = "ProjectName varchar(10), TrainsetNumber varchar(10), TrainDevice varchar(10), SoftwareVersion varchar(10)," \
                  " Mnemonic varchar(70), MediaFileLink varchar(70), MaintenanceHelp varchar(70)," \
                  " MaintenanceHelpFile varchar(70), TCodeName varchar(10), Description varchar(200)," \
                  " Name varchar(70), CategoryId varchar(70), CategoryName varchar(70), DeviceId varchar(10)," \
                  " DeviceName varchar(70), Counter varchar(70), OccurenceDate varchar(70)," \
                  " RecordDate varchar(70), HintId varchar(70), HintName varchar(70), SeverityId varchar(70)," \
                  " SeverityName varchar(70), FunctionId varchar(70), FunctionName varchar(70)," \
                  " LocationId varchar(10), LocationName varchar(70), LocationCode varchar(10)," \
                  " LocalEventId varchar(70), StackId varchar(70), StackName varchar(70)," \
                  " FaultStatus varchar(70), UnitName varchar(70), ContextualUrl varchar(70)," \
                  " DecorationError varchar(70), TrainSet_Id varchar(5), Traces text"
    mysql.create_table(field_title)

    # 加入数据
    for each in paths:
        file_name = each.split("/")[-1]
        field, values = get_field_values(each, file_name)
        mysql.insert_data(field, values)

        # 移动完处理好的文件
        # shutil.move(each, destination_folder)

    mysql.commit()
    mysql.close_connect()



if __name__ == '__main__':
    main()