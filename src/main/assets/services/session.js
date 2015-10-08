'use strict';

define(["angular", "js-cookie", "angular-route", ], function(angular, Cookies) {

    angular
        .module('webformz.session', [])
        .factory('sessionInjector', SessionInjector)
        .factory('sessionService', SessionService)
        .config(function($httpProvider) {
            $httpProvider.interceptors.push('sessionInjector');
        });

    SessionService.$inject = ['$rootScope'];
    function SessionService($rootScope) {
        var service = {};

        service.set = Set;
        service.get = Get;
        service.data = Data;

        if (Cookies.get("session")) {
            Set(Cookies.get("session"))
        }

        return service;

        function Set(newSession) {
            $rootScope._session_ = newSession

            if (newSession)
                Cookies.set("session", newSession)
            else
                Cookies.remove("session")
        }

        function Get() {
            return $rootScope._session_
        }

        function Data() {
            var raw = Get();

            if (!raw)
                return {}
            else
                return JSON.parse(atob(Get().split(".")[1]))
        }
    }

    SessionInjector.$inject = ['sessionService'];
    function SessionInjector(SessionService) {
        var sessionInjector = {
            request: function(config) {
                var session = SessionService.get()

                if (session) {
                    config.headers['X-Session'] = session
                }

                return config;
            },

            response: function(config) {
                if (config.headers()['x-session']) {
                    SessionService.set(config.headers()['x-session'])
                }

                return config
            }
        };

        return sessionInjector;
    }
})
