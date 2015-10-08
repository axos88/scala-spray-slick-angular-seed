'use strict';

define(["angular", "angular-route"], function(angular) {
    angular
        .module('webformz.authentication', [
            'webformz.session'
        ])
        .factory('authenticationInjector', AuthenticationInjector)
        .factory('authenticationService', AuthenticationService)
        .config(function($httpProvider) {
            $httpProvider.interceptors.push('authenticationInjector');
        });

    AuthenticationService.$inject = ['$http', '$rootScope', '$timeout', 'sessionService'];
    function AuthenticationService($http, $rootScope, $timeout, sessionService) {
        var service = {};

        service.Login = Login;
        service.Logout = Logout;
        service.isLoggedIn = isLoggedIn

        return service;

        function Login(username, password, callback) {
            var url = '/api/login'
            var params = { user: username, password: password }

            $http.post(url, params).then(callback, callback)
        }

        function isLoggedIn() {
            return !(sessionService.get() == null)
        }

        function Logout() {
            sessionService.set(null)
        }
    }

    AuthenticationInjector.$inject = ["$location"];
    function AuthenticationInjector($location) {
        var authenticationInjector = {
            responseError: function(config) {
                if (config.status == 401) {
                    console.log("Redirect")
                    $location.path("/login")
                }

                return config
            }
        };

        return authenticationInjector
    }

})
