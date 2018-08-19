# ToDoList

**개발환경**
  - JAVA 8
  - Spring Boot 3.0.4
  - JPA
  - gradle
- Frontend
    - Bootstrap 4.1.0
    - jQuery 3.2.1

**빌드**
```
$ ./gradlew clean build
```

**실행**
```
$ ./startup.sh
```

**API**
  * 할일 등록
  > POST /todolists
  
  | Property | Data Type | Mandatory |
  |:---------|:----------|:----------|
  | todo  | String    | Y  |
  | todoReference | String | Y |
  ----
  
  * 할일 수정
  > PUT /todolists/{listId}
  
  | Property | Data Type | Mandatory |
  |:---------|:----------|:----------|
  | todo  | String    | Y  |
  | todoReference | String | Y |
  ----
  
  * 할일 완료
  > PATCH /todolists/{listId}/complete

  
  * 할일 목록 (페이징)
  > GET /todolists
  > Response
  
  | Property  |
  |"content": [
        {
            "listId": String,
            "todoReference": String,
            "todo": String,
            "createdAt": "yyyy-MM-dd HH:mm:ss",
            "lastModifiedAt": "yyyy-MM-dd HH:mm:ss",
            "completed": boolean
        }|
  ----
  
  * 페이징 처리를 위한 count조회
  > GET /todolists/count
  > Response
  
  {
    "count" : Integer
  }
  ----


## 1. 요구사항
#### 1. 1. TODO List 
```
1. TODO 추가시 다른 TODO를 참조로 걸 수 있습니다.
2. 참조는 다른 TODO의 ID를 명시하는 형태로 표현합니다.
3. 사용자는 TODO를 수정할 수 있어야 합니다.
  3.1 TODO의 내용을 수정할 수 있어야 합니다.
  3.2 TODO의 참조를 변경할 수 있어야 합니다.
    3.2.1 TODO의 참조를 끊을 수 있어야 합니다.
    3.2.2 TODO의 참조를 다른 TODO로 변경 가능해야 합니다.    
4. 사용자는 TODO를 완료처리 할 수 있습니다. 다만, 참조가 걸린 TODO가 완료되지 않았다면 완료처리가 불가능합니다.
```
## 2. 접근방법
#### 2. 1. Backend
```
1. 참조를 확인/수정/끊을 수 있는 방법은 트리라고 생각했습니다. Table을 트리구조로 표현할 수 있는 방법을 찾다가 parent_id만 명시해놓고,
어플리케이션에서 상태변경 (참조를 확인해야 하는 부분) 에서 트리를 구성한 뒤에 탐색하는 방법으로 구현하기로 하였습니다.
1.1. 트리로 구성 시, 다진트리로 구현하였고, 탐색 시 재귀를 통해서 노드를 DTS로 탐색하도록 구현하였습니다.
2. parent_id는 제가 구성한 어플리케이션 프론트에서는 명시할 수 있는 방법이 없기 때문에 root node는 참조를 걸고 있는 id중 가장 먼저 생성된 노드를
기준으로 하였고, parent node 는 가장 나중에 생성된 노드를 기준으로 하였습니다.
2.1. 프론트에서 depth를 표현할 수 있으면 parent node 및 root node를 표현할 수 있을 것 같습니다.
3. 수정항목은 "할 일" Column 수정하되, 참조를 걸 수 있는 아이디도 함께 표현할 수 있도록 하였습니다.
3.1. Id를 수정하게 하고, 참조를 함께 걸도록 하려 했으나 id를 수정하도록 열어주는 것은 그리 좋지 않은 방법이라 생각하여 "할 일"에서만 수정이 가능하도록 하였습니다.
4. 페이징은 lastModifiedAt을 기준으로 최신 TODOLIST부터 노출이 되도록 구현하였습니다.
4.1. 페이징의 경우, 할 일이 추가될 경우 최근 것을 먼저 보여주는 것이 순서라고 생각하였고, pagination을 통한 구현방법으로는 최근 것을 기준으로 paging처리 하는 것이 가장 나을 것이라고 생각했습니다.
4.2. 할 일 추가시, 리스트에서 항목을 추가하는 것으로 구현했었으나, 리스트 갱신이 필요했기 때문에 항목 추가 시, "할 일 등록"후, 페이징된 "할 일 목록"을 가져와 
노드를 재구성하는 형식으로 구현하였습니다.

#### 2. 3. Service Layer
* Service Layer에서는 노드를 만들어주는 TreeMaker클래스와 node를 재귀적으로 탐색하는 NodeFinder클래스가 
* 어플리케이션에서 중심적인 역할을 수행합니다.
* TreeMaker 클래스와 NodeFinder클래스는 "완료처리"를 할 때만 tree를 생성하고 탐색합니다.

#### 2. 2. Repository Layer

* Query를 통해서 모든 것을 하려는 것보다는 어플리케이션에서 소스레벨로 표현해주는 것이 유지보수 및 관리에 편하다고 생각합니다.
* 그렇기 때문에 DB와의 결합도를 강하게 가져가는 것보다 어플리케이션 레벨에서 트리를 구현하였고, persistent layer에서 
* 필요한 데이터만 그때 그때 가져오는 형식으로 구현하였습니다.
* root node를 생성하기 위해서 todoReference의 가장 오래된 id항목을 가져오기 위한 findAllBiggerThan을 JPQL을 통해서 구현한 것 외에는
* JPA 기본 쿼리를 사용하였습니다.


#### 3. 1. Frontend

* 할 일 추가
* 할 일 수정 
  - 수정 시에는 {TODO} @id @id 형식으로 참조를 변경할 수 있으며 할 일 내용을 수정할 수 있다. ex) 집안일 @1 @3
* 완료 처리 
  - 완료 처리 불가능시에는 참조가 걸린 항목을 alert을 통해 noti해 준다.
  - 완료 처리 완료 시, alert을 통해 noti해 준다.
* bootstrap을 통해 테이블 생성 및 Javascript, JQuery로 노드를 생성하였습니다.
