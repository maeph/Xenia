angular.module('Xenia.Oauth')
    .controller('OauthCtrl', function($rootScope, $routeParams, XENIA_API_URL, Oauth) {
        var urlParams = new URLSearchParams(window.location.search);
        var code = urlParams.get('code');

        Oauth.save(code);
        $rootScope.shouldRefresh = true;
        var redirect = $routeParams.id ? $routeParams.redirect + "/" + $routeParams.id : $routeParams.redirect;
        window.location.replace("http://localhost:8000/app/#" + redirect);
    });
