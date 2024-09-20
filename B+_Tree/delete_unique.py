import random

# delete.csv 파일에 1부터 1000000까지 중복 없이 랜덤 키 값을 저장
def create_delete_csv(filename='delete_unique.csv', num_entries=1000000):
    try:
        # 중복 없이 1부터 1000000까지 랜덤한 키 num_entries개 선택
        keys = random.sample(range(1, 1000001), num_entries)
        
        with open(filename, 'w') as f:
            for key in keys:
                f.write(f"{key}\n")  # 키 값을 CSV 파일에 저장
        print(f"File {filename} created successfully with {num_entries} unique keys!")
    except Exception as e:
        print(f"An error occurred: {e}")

if __name__ == "__main__":
    create_delete_csv()
