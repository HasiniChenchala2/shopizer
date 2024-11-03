grammar SwaggerShopizer;

start: (get | post | put | delete ) EOF;

put 
    : putCatalog;

get
    : getCatalog | getManufacturer;

post
    : postCatalog;

delete
    : deleteCatalog;

getCatalog
    : 'curl -X GET "http://localhost:8080/api/v1/private/catalog/' id '" -H "accept: application/json"'
    ;

postCatalog
    : 'curl -X POST "http://localhost:8080/api/v1/private/catalog" -H "accept: application/json" -H "Content-Type: application/json" -d "{\\"code\\":\\"' codeValue '\\",\\"defaultCatalog\\":' booleanValue ',\\"id\\":' id ',\\"visible\\":' booleanValue '}"'
    ;

getManufacturer
    : 'curl -X GET "http://localhost:8080/api/v1/manufacturers/' id '" -H "accept: application/json"'
    ;

deleteCatalog 
    : 'curl -X DELETE "http://localhost:8080/api/v1/private/catalog/' id '"  -H "accept: application/json"'
    ;

putCatalog 
    : 'curl -X PUT "http://localhost:8080/api/v1/private/catalog" -H "accept: application/json" -H "Content-Type: application/json" -d "{\\"code\\":\\"' codeValue '\\",\\"defaultCatalog\\":' booleanValue ',\\"id\\":' id ',\\"visible\\":' booleanValue '}"'
    ;

codeValue
    : STRING_LITERAL;

booleanValue
    : 'true' | 'false';

id
    : INTEGER_LITERAL;

STRING_LITERAL
    : '"' (~["\r\n])* '"';

INTEGER_LITERAL
    : [0-9]+;