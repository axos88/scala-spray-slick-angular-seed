'use strict';

define(["angular", "angular-route", 'services/authentication'], function(angular) {
    angular.module('webformz.auth', ['ngRoute', 'webformz.authentication'])
        .config(['$routeProvider', function($routeProvider) {
            $routeProvider
                .when('/login', {
                    templateUrl: '/app/views/login.html',
                    controller: 'LoginCtrl'
                })
                .when("/logout", {
                    template: 'Logging out...',
                    controller: 'LogoutCtrl'
                })
        }])

        .controller('LoginCtrl', function($scope, authenticationService, $location, $rootScope) {
            if (authenticationService.isLoggedIn()) $location.path("/")

            $scope.login = function() {
                authenticationService.Login($scope.user, $scope.password, function(response) {
                    switch(response.status) {
                      case 200:
                        $location.path("/")
                        break;
                      case 401:
                        $rootScope.error = "Invalid username / password"
                        break
                      default:
                        if (response.data && response.data.msg) $rootScope.error = response.data.msg
                            else $rootScope.error = "Unknown error occured: " + JSON.stringify(response)
                    }
                })
            }
        })

        .controller('LogoutCtrl', function(authenticationService, $location) {
            authenticationService.Logout()
            $location.path("/login")
        })
})
