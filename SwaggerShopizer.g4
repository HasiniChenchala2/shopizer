grammar SwaggerShopizer;

start
    : CURL_COMMAND URL_HEADER ACCEPT_HEADER EOF;

CURL_COMMAND
    : 'curl -X GET';

URL_HEADER
    : '"http://localhost:8080/api/v1/private/catalog/' INTEGER_LITERAL '"';

ACCEPT_HEADER
    : '-H "accept: application/json"';

INTEGER_LITERAL 
    : '0' 
    | [1-9] [0-9]*;