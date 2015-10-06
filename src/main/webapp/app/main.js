'use strict';

var rs

require(["angular", "angular-route", "angular-websocket",
         "helpers/Array",
         "controllers/persons", "controllers/auth",
         "services/websocket", "services/authentication", "services/session"], function() {

    angular.module("webformz", [])
    angular.module('webformz.app', ['ngRoute', 'webformz.persons', 'webformz.auth', 'webformz.authentication'])
      .config(['$routeProvider', function($routeProvider) {
        $routeProvider.otherwise({redirectTo: '/persons'});
      }])
      .run(function($rootScope, authenticationService, sessionService, WebSocketService) {
         $rootScope.auth = authenticationService
         $rootScope.session = sessionService
         $rootScope.WebSocketService = WebSocketService
         rs = $rootScope
      })
      .run(function($rootScope, authenticationService) {
         $rootScope.$on('$routeChangeSuccess', function(ev,data) {
           if (data.$$route && data.$$route.controller)
             $rootScope.controller = data.$$route.controller;
         })
      })

    angular.bootstrap(document, ['webformz.app']);
})
