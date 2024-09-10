> # 로컬 환경에서 실행하는 법
>
> 1. Docker에서 MySql 실행
> 2. Docker에서 Redis 실행
> 3. jasypt.encryptor.password 주입

## 1. Docker에서 MySql 실행

```bash
# MySql 이미지 다운로드
docker pull mysql:latest

# MySql 컨테이너 생성 및 실행
docker run --name mysql-container -e MYSQL_ROOT_PASSWORD=<password> -d -p 3306:3306 mysql:latest

# MySql 컨테이너 접속
docker exec -it mysql-container mysql -u root -p
Enter password: <password>
```

## 2. Docker에서 Redis 실행

```bash
# Redis 이미지 다운로드
docker pull redis:latest

# Redis 컨테이너 생성 및 실행
docker run --name redis-container -d -p 6379:6379 redis:latest --requirepass <password>

# Redis 컨테이너 접속
docker exec -it redis-container redis-cli
127.0.0.1:6379> AUTH <password>
```

## 3. jasypt.encryptor.password 주입

```bash
# 터미널에 환경 변수 주입
# ⚠️환경 변수를 주입한 터미널로 실행해야 한다.
export ENCRYPT_KEY=<encrypt-key-value>
```

> 도움되는 명령어(컨테이너 이름 대신 id를 입력해도 된다)

```bash
# Docker 이미지 확인 명령어
docker images

# Docker 이미지 삭제 명령어
docker rmi <image-name>

# Docker 컨테이너 확인 명령어
# ps(동작중인 컨테이너만), ps -a(모든 컨테이너)
docker ps
docker ps -a

# 컨테이너 정지, 시작, 삭제 명령어
docker stop <container-name>
docker start <container-name>
docker rm <stoped-container-name or container>

# 프롬프트 탈출 명령어 -> 리눅스 쉘, CLI 탈출에 사용
exit
```
