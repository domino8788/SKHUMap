# SKHU Map
<img src="https://user-images.githubusercontent.com/26570319/92998388-85dafa00-f554-11ea-8749-f5745a967fdb.png" width="400px" height="400px">

본 프로젝트는 성공회대학교의 캠퍼스와 실내지도를 표시하는 지도앱인 SKHU Map 을 제작하는 프로젝트 입니다.<br>
2020년 1학기 소프트웨어 캡스톤디자인 과목에서 기획 및 진행 했으며 성공회대학교 총무처에서 데이터를 제공 받았습니다.<br>
아직 성공회대 캠퍼스를 실내까지 안내하는 앱이 없다는 사실에 근거해 캠퍼스가 익숙하지 않은 학우들을 위해 제작 되었습니다.<br>

# 개발자 / 기여자
<img src="https://user-images.githubusercontent.com/26570319/93020837-745c2580-f61a-11ea-96c9-756bdcec2a65.png" width="300px">

- 김남진(201432003, skawls1124@gmail.com)
  - 프로젝트 총괄, 기획, 앱 개발, 백엔드 개발, 디자인
  - 단독개발

# 사용한 기술
- 앱 이름 : SKHU Map
- 플랫폼 : Android
- 언어 : Kotlin
- DB : Cloud Firestore
- Backend : Cloud Functions, Firebase Hosting
- 유지보수 : Firebase Crashlytics
- 인증 : Firebase Authentication

# 릴리즈 정보
- 최종 릴리즈 버전 : 1.3.3 (2020-09-14)

### 1차 릴리즈 시연 영상
https://youtu.be/8vFSYhXAX8U

### 2차 릴리즈 시연 영상
https://youtu.be/iMRNe0X60qk

### 3차 릴리즈 시연 영상
https://youtu.be/6wn9quB179A

### 제11회 성공회대학교 IT&미디어콘텐츠 경진대회 발표 동영상
https://youtu.be/Hv1FgHqrGew

#### 동영상 발표 내용 순서
01. 작품 개요
02. 팀 소개(팀 이름 설명, 참여 인력)
03. 이용 기술
04. 개발 일정
05. 작품 구성
06. 작품 특징
07. 개발 방법
08. 작품 시연

# SKHU Map v1.3.0 기능 소개

## 캠퍼스, 실내지도 표시

|캠퍼스 지도와 실내지도|층 선택|시설 시간표|
|-|-|-|
![캠퍼스 지도와 실내지도](https://user-images.githubusercontent.com/26570319/93013090-393d0080-f5e0-11ea-8605-f41402aa4b3a.gif)|![층 선택](https://user-images.githubusercontent.com/26570319/93012945-3f7ead00-f5df-11ea-98b4-3e4f5a5bbfb7.gif)|![시설 시간표](https://user-images.githubusercontent.com/26570319/93012881-b1a2c200-f5de-11ea-97ac-a503d299f843.gif)

- 캠퍼스 지도에서 건물 클릭 시 해당 건물의 실내지도가 표시된다.
  - 실내지도에는 시설이 표시된다.
  - 지도 확대는 double tap과 spread를 통해 가능하다.
  - 지도 축소는 pinch를 통해 가능하다.
  - 실내지도 표시 중 뒤로가기 버튼 클릭 시 캠퍼스 지도로 돌아간다.
- 실내지도를 표시 중일 때 건물의 층을 선택할 수 있다.
- 실내지도에서 시설 클릭 시 해당 시설의 시간표가 표시된다.

## 검색
|검색|
|-|
|<img src="https://user-images.githubusercontent.com/26570319/93013160-d39d4400-f5e0-11ea-9388-6afd191210bf.gif" width="300px">|

- 상단의 검색바를 통해 시설을 검색할 수 있다.
  - 검색바는 자동완성기능을 지원한다.
  - 자동완성 목록을 클릭하면 해당 시설이 지도에 표시된다.

## 즐겨찾기
|즐겨찾기 추가|즐겨찾기 수정|즐겨찾기 삭제|
|-|-|-|
|![즐겨찾기 추가](https://user-images.githubusercontent.com/26570319/93013247-98e7db80-f5e1-11ea-95b1-07d9c2bdd7a0.gif)|![즐겨찾기 수정](https://user-images.githubusercontent.com/26570319/93013277-e8c6a280-f5e1-11ea-82b1-3e011d55f35e.gif)|![즐겨찾기 삭제](https://user-images.githubusercontent.com/26570319/93013322-6094cd00-f5e2-11ea-94ee-f5b0842cde07.gif)


- 실내지도에서 시설 클릭 시 표시되는 시간표에서 즐겨찾기가 가능하다.
  - 즐겨찾기 아이콘을 다시 클릭하면 즐겨찾기가 해제된다.
- 교내지도 탭에서 즐겨찾기 목록이 표시된다.
  - 즐겨찾기 클릭 시 지도에 해당 시설이 표시된다.
  - 즐겨찾기를 수정할 수 있다.
    - 즐겨찾기 목록에 표시되는 순서를 변경할 수 있다.
      - 우측의 버튼을 클릭한 상태로 스크롤하면 즐겨찾기의 순서를 변경할 수 있다.
    - 즐겨찾기를 삭제할 수 있다.
      - 스와이핑으로 삭제가 가능하다.
      - 콤보박스가 체크된 즐겨찾기를 일괄삭제 할 수 있다.
      
## 캘린더
|캘린더 인터페이스|일정 추가|일정 수정|일정 삭제|
|-|-|-|-|
|![캘린더 인터페이스](https://user-images.githubusercontent.com/26570319/93016559-2258d700-f5fd-11ea-911e-0bec1581d438.gif)|![일정 추가](https://user-images.githubusercontent.com/26570319/93016710-39e48f80-f5fe-11ea-9bb1-a015528df9c1.gif)|![일정 수정](https://user-images.githubusercontent.com/26570319/93017642-4cae9280-f605-11ea-9e7c-472075918646.gif)|![일정 삭제](https://user-images.githubusercontent.com/26570319/93017661-6e0f7e80-f605-11ea-89e6-4b50a8074d1c.gif)

- 개인 일정을 표시할 수 있다.
  - 개인일정을 생성할 수 있다.
    - 특정날짜에만 진행되는 일정을 생성할 수 있다.
    - 일정기간 동안 매주 반복되는 일정을 생성할 수 있다.
  - 개인일정을 수정할 수 있다.
    - 일정의 타입(매주 반복 일정, 당일 일정)을 변경할 수 있다.
    - 일정의 날짜 및 시간을 수정할 수 있다.
    - 일정의 제목 및 내용을 수정할 수 있다.
  - 개인일정을 삭제할 수 있다.
    - 만료된지 한달된 일정은 자동으로 삭제된다.
- 학사행정시스템 연동을 통해 학생 시간표가 표시된다.
  - 학생 시간표를 수정할 수 있다.

## 학사행정시스템 연동
|자동 로그인|회원가입|로그아웃|회원탈퇴|
|-|-|-|-|
|![자동 로그인](https://user-images.githubusercontent.com/26570319/93017802-6f8d7680-f606-11ea-82a2-28dd0b2d9598.gif)|![회원가입](https://user-images.githubusercontent.com/26570319/93017770-2fc68f00-f606-11ea-93a2-b7b92ebeac75.gif)|![로그아웃](https://user-images.githubusercontent.com/26570319/93017848-c98e3c00-f606-11ea-8e2a-a04061989b0a.gif)|![회원탈퇴](https://user-images.githubusercontent.com/26570319/93017815-8633cd80-f606-11ea-8b63-97cf06743e2c.gif)

  - 앱 시작 시 표시되는 로그인 페이지에서 학사행정시스템 계정정보를 통해 회원가입 또는 로그인 할 수 있다.
    - 최초 로그인 시 회원가입이 진행된다.
    - 인증정보가 유효하면 다음에 앱 시작 시 자동 로그인 된다.
    - 학사행정시스템 인증정보 변경 시 기존 비밀번호를 입력하는것으로 계정정보 갱신이 가능하다.
  - 학사행정시스템 계정정보를 통해 서버와 유저 데이터(즐겨찾기, 일정, 학생 시간표)를 동기화 할 수 있다.

# SKHU Map v1.3.3 기능 소개

## 실내지도에서 건물정보 표시

|건물정보|
|-|
|<img src="https://user-images.githubusercontent.com/26570319/93021016-707cd300-f61b-11ea-82a2-91868ff487d7.gif" width="300px">|
