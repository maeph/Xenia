angular.module('Xenia.Dashboard')
    .controller('DashboardCtrl', function(Event) {
        var dashboard = this;

        dashboard.events = [];
        dashboard.isRefreshing = false;


        dashboard.init = function() {
            dashboard.getEvents();
        };

        dashboard.getEvents = function() {
            Event.listAll().then(function(result){
                dashboard.events = result.data;
            });
        };

        dashboard.refreshEvents = function() {
            dashboard.isRefreshing = true;
            Event.refreshAll().then(function(){
                dashboard.getEvents();
                dashboard.isRefreshing = false;
            },
            function(error) {
                window.location.replace("https://secure.meetup.com/oauth2/authorize" +
                    "?client_id="+ error.data.clientId +
                    "&response_type=code" +
                    "&redirect_uri=http://localhost:8000/app/#/oauth2/dashboard");
            });
        };

        dashboard.init();
    });