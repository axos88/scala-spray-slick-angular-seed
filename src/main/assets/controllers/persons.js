'use strict';

define(["angular", "angular-route", "services/websocket"], function(angular) {
    angular.module('webformz.persons', ['ngRoute', 'webformz.websocket', 'webformz.authentication'])
        .config(['$routeProvider', function($routeProvider) {
            $routeProvider.when('/persons', {
                templateUrl: '/assets/webformz/views/persons.html',
                controller: 'PersonsCtrl'
            })
        }])

        .controller('PersonsCtrl', function($rootScope, $scope, $location, $http, WebSocketService) {

            $scope.newperson = { fname: "", lname: ""}

            //	if (!AuthenticationService.isLoggedIn())
            //	{
            //		$location.path('/login')
            //		$rootScope.errorAfterPageLoad = "Need to log in"
            //		return
            //	}
            //	$scope.session = sessionService.get()
            $scope.reloadPersons = function() {
                $http.get("/api/persons").then(function(response) {
                    $scope.persons = response.data
                })
            }

            $scope.addPerson = function() {
                var data = $scope.newperson
                $http.post("/api/persons/new", data).then(null, function(response) {
                    console.log(response)
                })
            }

            $scope.deletePerson = function(id) {
                $http.delete("/api/persons/" + id).then(null, function(response) {
                    console.log(response)
                })
            }

            var wsSubscriber = WebSocketService.subscribe("persons", $scope.reloadPersons)

            $scope.$on('$destroy', function() {
                WebSocketService.unSubscribe(wsSubscriber)
            });

            $scope.reloadPersons()
    });

})
