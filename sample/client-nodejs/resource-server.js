var express = require('express');
var app = express();

var credentials = {
  clientID: 'sample-client-cli',
  clientSecret: 'AAAAAA123456',
  site: 'http://localhost:8088'
};

var oauthClient = require('./lib/oauth_client_credentials')(credentials);


app.get('/ping', function (req, res) {
  oauthClient.authorized(req, res, function(req, res){
    res.send('pong!!!');
  })

})

var server = app.listen(3000, function () {

  var host = server.address().address
  var port = server.address().port

  console.log('Example app listening at http://%s:%s', host, port)

})
