import pymysql
import os
import shutil

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
        self.cursor.execute("alter table trainevents modify COLUMN Traces text")
        try:
            # TODO
            # self.cursor.execute("ALTER TABLE trainevents ADD PRIMARY KEY (`ProjectName`, `TrainsetNumber`, `TrainDevice`, `Mnemonic`, `OccurenceDate`, `TCodeName`, `LocationCode`)")
            self.cursor.execute("ALTER TABLE trainevents ADD CONSTRAINT unique_values UNIQUE (`ProjectName`, `TrainsetNumber`, `TrainDevice`, `Mnemonic`, `OccurenceDate`, `TCodeName`, `LocationCode`)")
        except:
            print("unique data already satisfied")
        self.conn.commit()


    def insert_data(self, input_titles, input_content):
        # 将数据填入表
        for i in input_content:
            data_line = ''

            for j in i:
                if j == '':  # 如果是空字符串, 就给一个值
                    j = "'null'"
                data_line += j + ','
            data_line = data_line.rstrip(',').strip().replace("\"", "\'")

            try:
                self.cursor.execute(f"insert into trainevents({input_titles}) values({data_line})")
            except Exception as e:
                print(f"{input_titles=}")
                print(f"{data_line=}")
                print(e)
                print("\n")

            self.conn.commit()

        self.conn.commit()


    def close_connect(self):
        self.conn.commit()
        self.cursor.close()
        self.conn.close()



# 获取文本标题 (已转换成mysql字段)
def get_title(first_line):
    str_title = ''
    # 加上文件名字
    str_title += '`ProjectName`, `TrainsetNumber`, `TrainDevice`, `SoftwareVersion`, '

    # 处理文件标题
    first_line = first_line.replace("\ufeff", "")
    list_title = first_line.split(';')
    list_title = [f"`{each}`" for each in list_title]
    for each in list_title:
        each = each.replace('.', '_')
        str_title += each + ', '
    str_title = str_title.rstrip(', ').strip()
    return str_title


# 获取文本内容和标题 (数组形式)
def get_title_content(input_path, input_file_name):
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

                # 插入文件基础信息
                a, b, c, d, *_ = input_file_name.split("_")
                line = [repr(a), repr(b), repr(c), repr(d)] + line
                text_content.append(line)

        # 读取完文件将最后的end_of_line插入进text_content
        if end_of_line != '':
            text_content[-1].append(repr(end_of_line))
            end_of_line = ''

    getTitle = get_title(first_line)
    return getTitle, text_content




if __name__ == '__main__':
    paths = os.listdir("./unhandled")
    destination_folder = "./handled"

    # 去除所有非csv文件
    paths = ["./unhandled/" + each for each in paths if each.split(".")[-1] == "csv"]

    mysql = Mysql("root", "root")
    mysql.connect_mysql_db()


    field_title = "ProjectName varchar(26), TrainsetNumber varchar(26), TrainDevice varchar(26), SoftwareVersion varchar(26)," \
                  " Mnemonic varchar(100), MediaFileLink varchar(256), MaintenanceHelp varchar(256)," \
                  " MaintenanceHelpFile varchar(256), TCodeName varchar(26), Description varchar(256)," \
                  " Name varchar(256), CategoryId varchar(256), CategoryName varchar(256), DeviceId varchar(26)," \
                  " DeviceName varchar(26), Counter varchar(256), OccurenceDate varchar(100)," \
                  " RecordDate varchar(100), HintId varchar(256), HintName varchar(256), SeverityId varchar(256)," \
                  " SeverityName varchar(256), FunctionId varchar(256), FunctionName varchar(256)," \
                  " LocationId varchar(26), LocationName varchar(256), LocationCode varchar(26)," \
                  " LocalEventId varchar(256), StackId varchar(256), StackName varchar(256)," \
                  " FaultStatus varchar(256), UnitName varchar(256), ContextualUrl varchar(256)," \
                  " DecorationError varchar(256), TrainSet_Id varchar(26), Traces text"
    mysql.create_table(field_title)

    # 加入数据
    for each in paths:
        file_name = each.split("/")[-1]
        title, content = get_title_content(each, file_name)
        mysql.insert_data(title, content)

        # 移动完处理好的文件
        # shutil.move(each, destination_folder)

    mysql.close_connect()