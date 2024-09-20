import random

# delete.csv 파일에 1부터 1000000까지 랜덤 키 값을 저장
def create_delete_csv(filename='delete.csv', num_entries=1000000):
    try:
        with open(filename, 'w') as f:
            for _ in range(num_entries):
                key = random.randint(1, 1000000)  # 1부터 1000000 사이의 정수 키 생성
                f.write(f"{key}\n")  # 키 값을 CSV 파일에 저장
        print(f"File {filename} created successfully!")
    except Exception as e:
        print(f"An error occurred: {e}")

if __name__ == "__main__":
    create_delete_csv()
