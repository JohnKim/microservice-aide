var credentials = {
  clientID: 'sample-client-cli',
  clientSecret: 'AAAAAA123456',
  site: 'http://localhost:8088'
};

var oauthClient = require('./lib/oauth_client_credentials')(credentials);

oauthClient.api('GET', 'http://localhost:8081/ping', {}, function(error, result, token) {

  console.log(error, result, token);

  oauthClient.checkToken(token+'', function(error, statusCode, result){

    console.log(error, statusCode, result);

  });

});
