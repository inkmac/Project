import pymysql
import os
import shutil


# 获取文本标题 (已转换成mysql字段)
def get_title(input_path):
    with open(input_path, mode='r', encoding="UTF-8") as input_file:
        first_line = input_file.readline().strip()
        first_line = first_line.replace("\ufeff", "")
        list_title = first_line.split(';')
        list_title = [f"`{each}`" for each in list_title]
        str_title = ''
        for each in list_title:
            each = each.replace('.', '_')
            str_title += each + ', '
        str_title = str_title.rstrip(', ').strip()  #.replace("\"", "\'")  # TODO
    return str_title


def get_field_title(input_path):
    with open(input_path, mode='r', encoding="UTF-8") as input_file:
        first_line = input_file.readline().strip()
        first_line = first_line.replace("\ufeff", "")
        list_title = first_line.split(';')
        field_str_title = ''
        for each in list_title:
            each = each.replace('.', '_')
            field_str_title += each + ' varchar(256), '
        field_str_title = field_str_title.rstrip(', ').strip()
    return field_str_title


# 获取文本内容和标题 (数组形式)
def get_title_content(input_path: str):
    text_content = []
    with open(input_path, mode='r', encoding="UTF-8") as input_file:
        first_line = input_file.readline()   # 第一行不要

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

            # 如果是正常的, 就将末尾元素插入
            if not line.startswith(';;;;;;;;;;'):
                line = line.replace("\"", "'")  # "改成', 作为一个单独数据
                # 正常加入
                line = line.split(';')   # 由于最后有一个分号, 数据会多出来
                line.pop()     # 去除最后的数据
                if end_of_line != '':
                    text_content[-1].append(repr(end_of_line))
                    end_of_line = ''
                text_content.append(line)

        # 读取完文件将最后的end_of_line插入进text_content
        if end_of_line != '':
            text_content[-1].append(repr(end_of_line))
            end_of_line = ''

    getTitle = get_title(input_path)
    return getTitle, text_content



def create_table(input_titles):
    # 连接mysql
    conn = pymysql.connect(host="127.0.0.1", port=3306,
                           user="root", password="root", charset="utf8")
    cursor = conn.cursor()

    # 创建数据库和表
    try:
        cursor.execute('create database eventmanagement')
    except Exception:
        print("database already satisfied")
    cursor.execute('use EventManagement')
    try:
        cursor.execute(f'create table trainevents({input_titles})')
    except Exception:
        print("table already satisfied")

    cursor.execute("alter table trainevents modify COLUMN Traces text")

    conn.commit()
    cursor.close()
    conn.close()

    # 计算表格的列数
    count_list = input_titles.split(",")
    count = len(count_list)
    return count


def insert_data(input_titles, input_content):
    conn = pymysql.connect(host="127.0.0.1", port=3306,
                           user="root", password="root", charset="utf8")
    cursor = conn.cursor()
    cursor.execute('use EventManagement')

    # 将数据填入表
    for i in input_content:
        data_line = ''

        for j in i:
            if j == '':    # 如果是空字符串, 就给一个值
                j = "'null'"
            data_line += j + ','
        data_line = data_line.rstrip(',').strip().replace("\"", "\'")

        try:
            cursor.execute(f"insert into trainevents({input_titles}) values({data_line})")
        except Exception as e:
            print(f"{input_titles=}")
            print(f"{data_line=}")
            print(e)
            print("\n")

        conn.commit()

    conn.commit()

    # 关闭连接
    cursor.close()
    conn.close()


if __name__ == '__main__':
    paths = os.listdir("./unhandled")
    destination_folder = "./handled"

    # 去除所有非csv文件
    paths = ["./unhandled/" + each for each in paths if each.split(".")[-1] == "csv"]

    # 获取最全的字段
    titles = ''
    for each in paths:
        if titles < (temp_title := get_field_title(each)):
            titles = temp_title
    create_table(titles)

    # 加入数据
    for each in paths:
        title, content = get_title_content(each)
        insert_data(title, content)

        # 移动完处理好的文件
        # shutil.move(each, destination_folder)

