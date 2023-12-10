1. 토큰기반 인증  
인증에 토큰을 사용하는 방식이다.  
토큰은 클라이언트를 구분하는데 사용하는 유일한 값이다.  
서버에서 생성해서 클라이언트에게 제공한 뒤, 클라이언트는 서버에 요청할 때마다 요청 내용과 함께 토큰을 정송한다.  
서버에서는 토큰으로 유효한 사용자인지 검증한다.  

2. JWT
JWT는 토큰 기반 인증에서 주로 사용하는 토큰이다.  
JSON 형식으로 사용자(클라이언트)의 정보를 저장한다.  
JWT는 헤더, 내용, 서명 구조로 이루어져 있다.  
헤더 : 토큰의 타입, 해싱 알고리즘 지정하는 정보 포함  
내용 : 토큰에 담을 정보가 들어감  
서명 : 토큰이 조작되지 않음을 확인하는 용도  

3. 리프레스 토큰  
액세스 토큰과 별개의 토큰  
액세스 토큰이 만료되었을때 새로운 액세스 토큰을 발급받는 용도로 사용한다.  

4. 필터  
실제로 요청이 전달되기 전과 후에 URL 패턴에 맞는 요청을 처리하는 기능 제공.  

5. 시큐리티 콘텍스트  
인증 객체가 저장되는 보관소.  
인증정보가 필요할때 언제든지 인증 객체를 꺼내어 사용하도록 제공되는 클래스.  
이러한 시큐리티 컨텍스트 객체를 저장하는 객체가 시큐리티 컨텍스트 홀더이다.