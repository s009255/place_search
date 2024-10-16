# 프로젝트 설명:
- ## 디렉터리 구조
    - com.test.place 하위 폴더로 정리
        - client: webclient 이용 외부 서버 호출
        - component: service, repository, controller 외 component들
        - config: Configuration class들 모음
        - controller: RestController 모음
        - dto: Data Transfer Object 모음
        - service: Service 모음

- ## 설명:
    - ### 프로그램의 지속적 유지 보수 및 확장 용이를 위한 설계:
        - client 추가를 위한 고려
          - abstract class로 client를 상속받아 구현
          - client 추가 시, 기본적인 url, path, param, header 처리, 그리고 response 처리만 구현
        - provider 추가를 위한 고려
          - PlaceService interface를 구현하여 provider 추가
          - provider 추가 시, getPlaces를 이용한 Place 변환만 추가.
          - priority를 이용한 우선 순위 변경 가능
        - 데이터 저장 및 cache를 redis만 사용
          - Flux를 이용 여러 검색 제공자에서 얻은 결과를 비동기적으로 병합하여 처리하는 방식 선택
    - ### 동시성 이슈를 위한 고려:
        - 다중 명령에 대한 원자성 보장
          - keyword 검색 수를 redis INCR 명령으로 처리
    - ### API 제공자의 오류 발 상황에 대한 고려
      - 각 client call 및 provider에서 오류 발생시 빈 list 반환 처리
      - client call의 성공한 결과(검색 결과가 비어있는 경우 제외)를 cache에 저장하여, 검색 요청 최소화.
    - ### 대용량 트래픽 처리를 위한 고려
      - WebFlux를 이용한, non blocking 처리
        - web client를 사용, 외부 API 호출 non blocking 처리
        - 키워드를 비동기적으로 Redis에 저장하는 부분에서, 논블로킹 방식으로 처리하여 메인 쓰레드의 부하 감소
      - 확장성 고려하여 내부 cache가 아닌 redis를 활용
    - ### 테스트 코드
      - 각 provider, keyword 검색 기능 테스트
      - API 3개일 경우 test code 작성
    - ### 새로운 검색 API 제공자의 추가 상황 고려
      - 외부 API 추가가 KeywordService 및 PlaceService 영향 X
      - 외부 API 변경 반영에도, 각 해당 client, provider만 수정
    - ### 그 외 고려
      - redis cahce 이용을 aspect로 처리하여, 각 client, provider에서 cache 처리를 하지 않아도 됨
      - 이 후 Place 외의 cache가 필요한 경우에도 해당 코드 활용 가능
      - redis 오류로 keyword count 및 cache 저장 조회 비정상 때에도, 검색 기능은 가능하도록 처리

- ## 추가 개발이 필요한 사항:
  - redis 실패 처리
    - keyword count 처리는 main 기능이니, redis 실패 처리가 추가되어야 함
  - Kakao, Naver, Google 같은 외부 API 실패 보완
    - 현재는 실패 시 빈 list 반환 처리만 되어 있음
    - 장애 발생 시 fallback 처리, 재시도 로직 등을 추가할 필요가 있
  - 테스트 커버리지 보완
    - 테스트 코드 추가 필요
  
- ## API Test:
  - 1) 장소 검색: curl --location -XGET 'localhost:8080/places/search?keyword=test'
  - 2) 검색 키워드 목록: curl --location -XGET 'localhost:8080/places/keywords'


