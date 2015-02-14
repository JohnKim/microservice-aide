var exports = module.exports,
    request = require('request');

module.exports = function(config) {

  var grant_type = 'client_credentials';
  var access_token = '';

  function api(method, path, params, callback) {

    if (typeof params === 'function') {
      callback = params;
      params = {};
    }

    if(!callback || typeof(callback) !== 'function') {
      throw new Error('Callback not provided on API call');
    }

    call(method, config.site + '/oauth/token', params, function(error, response, body) {

      if (error) {
        return callback(error);
      }

      try      { body = JSON.parse(body); }
      catch(e) {  }

      if (response.statusCode >= 400) return callback(body || new errors.HTTPError(response.statusCode), null)

      delete params.grant_type;
      params.access_token = body.access_token;

      access_token = body.access_token

      call(method, path, params, function(error, response, body) {
        callback(error, body, access_token);
      });

    });
  }

  function checkToken(token, callback) {

    var params = {};
    params.token = token;
    call('POST', config.site + '/oauth/check_token', params, function(error, response, body) {
      callback(error, response.statusCode, body);
    });

  }

  function authorized(req, res, callback) {

    var token = null;
    if(req.headers.authorization){
      var parts = req.headers.authorization.split(' ');
      if (parts.length == 2) {
          var scheme = parts[0];
          var credentials = parts[1];

          if (/^Bearer$/i.test(scheme)) {
              token = credentials;

              checkToken(token, function(error, statusCode, body){

                console.log(error, statusCode, body);

                if(statusCode == 200){
                  callback(req, res);
                }else{
                  res.send(statusCode, body);
                }

              });

          } else{
            res.send(400, 'not available');
          }
      } else {
        res.send(400, 'Format is Authorization: Bearer [token]');
      }
    } else {
      res.send(400, 'not available');
    }

  };

  function call(method, url, params, callback) {

    var options = { uri: url, method: method }
    if (!config.clientID || !config.clientSecret || !config.site)
      throw new Error('Configuration missing. You need to specify the client id, the client secret and the oauth2 server');

    if (params.access_token) {
      options.headers = { 'Authorization': 'Bearer ' + params.access_token };
      delete params.access_token;
    } else if (config.clientID && config.clientSecret) {
      options.headers = { 'Authorization': 'Basic ' + new Buffer(config.clientID + ':' + config.clientSecret).toString('base64') };
      params.grant_type = 'client_credentials';
    } else {
      options.headers = {};
    }

    if (config.agent)
      options.agent = config.agent;

    if (isEmpty(params)) params = null;
    if (method != 'GET') options.form = params;
    if (method == 'GET') options.qs   = params;

    request(options, callback)
  }

  function configure(config) {
    config = config || {};
    return config;
  }

  config = configure(config);

  function mergeDefaults(o1, o2) {
    for (var p in o2) {
      try { if (typeof o2[p] == 'object') { o1[p] = mergeDefaults(o1[p], o2[p]); } else if (typeof o1[p] == 'undefined') { o1[p] = o2[p]; } }
      catch(e) { o1[p] = o2[p]; }
    }
    return o1;
  }

  function isEmpty(ob){
    for(var i in ob){ return false;}
    return true;
  }

  return {
    'api': api,
    'checkToken': checkToken,
    'authorized': authorized
  }
};
