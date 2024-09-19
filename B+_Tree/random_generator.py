import random

# 100만개의 (key, value) 쌍 생성 및 index.txt 파일에 저장
def create_index_txt_file(filename='index1.txt', num_entries=1000000):
    try:
        with open(filename, 'w') as f:  # 텍스트 모드로 파일 열기
            generated_keys = set()  # 생성된 키를 추적하기 위한 set
            for _ in range(num_entries):
                # 중복되지 않는 키 생성
                while True:
                    key = random.randint(1, 1000000)  # 1부터 100만 사이의 정수 키 생성
                    if key not in generated_keys:
                        generated_keys.add(key)  # 키를 set에 추가
                        break  # 중복되지 않는 키를 찾았으므로 루프 종료
                
                value = random.randint(1, 1000)  # 1부터 10000 사이의 정수 값 생성
                
                # 파일에 key-value 쌍을 문자열로 작성
                f.write(f"{key},{value}\n")
        print(f"File {filename} created successfully!")
    except Exception as e:
        print(f"An error occurred: {e}")

if __name__ == "__main__":
    create_index_txt_file()
