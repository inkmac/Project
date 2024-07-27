import pymysql
import os
import shutil

# 获取文本标题 (已转换成mysql字段)
def get_title(input_path):
    with open(input_path, mode='r', encoding="UTF-8") as input_file:
        first_line = input_file.readline()
        first_line_split = first_line.split(';')
        title = ''     # 转换成表格字段
        for each in first_line_split:
            each = each.replace('.', '_')
            title += each + ' varchar(256), '
        title = title.rstrip(', ')
        return title


# 获取文本内容 (数组形式)
def get_content(input_path: str):
    text_content = []
    with open(input_path, mode='r', encoding="UTF-8") as input_file:
        first_line = input_file.readline()   # 第一行不要
        for line in input_file:
            end_of_line = ''
            if line.startswith(';;;;;;;;;;'):
                line = line.lstrip(';')
                line = line.rstrip('\n')
                end = line.replace(';', ':', 1)   # 只替换一个; 也就是第一个
                end_of_line += end
                continue

            # 如果是正常的, 就将末尾元素插入
            if not line.startswith(';;;;;;;;;;'):
                # 正常加入
                line = line.split(';')
                line.pop()    # 去除最后的\n
                if text_content:
                    text_content[-1].append(end_of_line)
                text_content.append(line)

        return text_content


def create_table(input_titles):
    # 连接mysql
    conn = pymysql.connect(host="127.0.0.1", port=3306,
                           user="root", password="root", charset="utf8")
    cursor = conn.cursor()

    # 创建数据库和表
    try:
        cursor.execute('create database EventManagement')
        cursor.execute('use EventManagement')
        cursor.execute(f'create table TrainEvents({input_titles})')
    except Exception:
        print("database and table already satisfied")

    conn.commit()
    cursor.close()
    conn.close()

    # 计算表格的列数
    count_list = input_titles.split(",")
    count = len(count_list)
    return count


def insert_data(input_content: list[[]], counts):
    conn = pymysql.connect(host="127.0.0.1", port=3306,
                           user="root", password="root", charset="utf8")
    cursor = conn.cursor()
    cursor.execute('use EventManagement')

    # 将数据填入表
    for i in input_content:
        data_line = ''
        if len(i) < counts:  # 如果列数不够就补上
            need_add_count = counts - len(i)
            for add in range(need_add_count):
                i.append('null')

        for j in i:
            if j == '':    # 如果是空字符串, 就给一个值
                j = repr('NA')
            data_line += j + ','
        data_line = data_line.rstrip(',')
        cursor.execute(f'insert into trainevents values({data_line})')

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
        if titles < (temp_title := get_title(each)):
            titles = temp_title
    rows = create_table(titles)

    # 加入数据
    for each in paths:
        content: list[[]] = get_content(each)
        try:
            insert_data(content, rows)
        except Exception as e:
            print(e)
            pass

        # 移动完处理好的文件
        shutil.move(each, destination_folder)


