angular.module('Xenia.Common')
    .service('Oauth', function($http, XENIA_API_URL) {
        var oauth = this;


        oauth.save = function(code) {
            console.log(code);
            return $http.get(XENIA_API_URL + "/oauth2/redirect?code=" + code);
        };

    });