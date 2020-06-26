#Pay-Api
###
##3 가지 api 제공
1 토큰 생성 api
  - 입력한 금액과 사람 수로 받기 위한 토큰을 생성합니다.  
   [ request example ]  
    POST /api/v1/tokens HTTP/1.1  
    Host: localhost  
    X-USER-ID: 1  
    X-ROOM-ID: room-1  
    Content-Type: application/json      
    
    { "money":"100",  "peopleCount":"2"}  
  
    [ response example ]
    
      
    {
        "status": 201,
        "url": "/api/v1/tokens",
        "data": {
            "token": "1k1tzcrg9m6w5"
        },
        "timestamp": "2020-06-27 00:45:14"
    }
    
2 받기 api
  - 입력한 토큰으로 금액을 받습니다.  
  [ request example ]  
    PUT /api/v1/tokens/{token} HTTP/1.1  
    Host: localhost  
    X-USER-ID: 2  
    X-ROOM-ID: room-1   
  [ response example ]  
  
  
    {
      "status": 200,
      "url": "/api/v1/tokens/1huzyzf09jho2",
      "data": {
          "money": 34
      },
      "timestamp": "2020-06-27 00:57:22"
    }
  

3 조회 api
  - 발행한 토큰에 대한 정보를 받습니다.  
  [ request example ]    
  PUT /api/v1/tokens/{token} HTTP/1.1    
  Host: localhost  
  X-USER-ID: 1    
  X-ROOM-ID: room-1     
  [ response example ]
  
  
    {
       "status": 200,
       "url": "/api/v1/tokens/1huzyzf09jho2",
       "data": {
           "publishedAt": "2020-06-27T00:57:18",
           "money": 100,
           "publishedMoney": 34,
           "publishedInfos": [
               {
                   "userId": 2,
                   "money": 34
               }
           ]
       },
       "timestamp": "2020-06-27 01:01:24"
    }
   
 
