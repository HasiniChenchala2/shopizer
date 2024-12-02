grammar Shopizer;

start: (get | post | put | delete) EOF;

get
    : getCatalog | getManufacturer | getCategoryByProduct | getAllCategories | getCustomerReviews;

post
    : postCatalog | postContact | postCart | postCustomerLogin;

put
    : patchCatalog | putManufacturer;

delete
    : deleteCatalog  | deleteCategory | deleteCart | deleteCustomer;

/* GET Requests */
getCatalog
    : '{ "id": ' id ' }';

getManufacturer
    : '{ "id": ' id ' }';

getCategoryByProduct
    : '{ "id": ' id ' }';

getAllCategories
    : '{ "count": ' count ', "page": ' page ' }';

getCustomerReviews
    : '{ "id": ' id ' }';

/* POST Requests */
postCatalog
    : '{ "code": ' codeValue ', "defaultCatalog": ' booleanValue ', "id": ' id ', "visible": ' booleanValue ' }';

postContact
    : '{ "comment": ' commentValue ', "email": ' emailValue ', "name": ' nameValue ', "subject": ' subjectValue ' }';

postCart
    : '{ "attributes": [{ "id": ' id ' }], "product": ' productValue ', "promoCode": ' promoCodeValue', "quantity": ' quantity ' }';

postCustomerLogin
    : '{ "password": ' passwordValue ', "username": ' usernameValue ' }';

/* PATCH (PUT) Requests */
patchCatalog 
    : '{ "code": ' codeValue ', "defaultCatalog": ' booleanValue ', "id": ' id ', "visible": ' booleanValue ' }';

putManufacturer
    : '{ "code": ' codeValue ', "descriptions": [{ "description": ' descriptionValue ', "friendlyUrl": ' friendlyUrlValue ', "highlights": ' highlightsValue ', "id": ' id ', "keyWords": ' keyWordsValue ', "language": ' languageValue ', "metaDescription": ' metaDescriptionValue ', "name": ' nameValue ', "title": ' titleValue '}], "id": ' id ', "order": ' orderValue ' }';

/* DELETE Requests */
deleteCatalog 
    : '{ "id": ' id ' }';

deleteCategory 
    : '{ "id": ' id ' }';

deleteCart
    : '{ "code": ' codeValue ', "sku": ' skuValue ' }';

deleteCustomer
    : '{ "id": ' id ' }';

/* Values */
codeValue
    : STRING_LITERAL;

skuValue
    : INTEGER_LITERAL;

quantity
    : INTEGER_LITERAL;

id
    : INTEGER_LITERAL;

booleanValue
    : 'true' | 'false';

commentValue
    : STRING_LITERAL;

emailValue
    : STRING_LITERAL;

nameValue
    : STRING_LITERAL;
    
usernameValue
    : STRING_LITERAL;

subjectValue
    : STRING_LITERAL;

descriptionValue
    : STRING_LITERAL;

friendlyUrlValue
    : STRING_LITERAL;

highlightsValue
    : STRING_LITERAL;

keyWordsValue
    : STRING_LITERAL;

languageValue
    : STRING_LITERAL;

passwordValue
    : STRING_LITERAL;

promoCodeValue 
    : STRING_LITERAL;

metaDescriptionValue
    : STRING_LITERAL;

titleValue 
    : STRING_LITERAL;

productValue
    : STRING_LITERAL;

orderValue
    : INTEGER_LITERAL;

count
    : INTEGER_LITERAL;

page
    : INTEGER_LITERAL;

STRING_LITERAL
    : '"' (~["\\\r\n])* '"';

INTEGER_LITERAL
    : [1-9]+ ;
