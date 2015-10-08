'use strict';

define(["angular", "angular-websocket", "helpers/Array"], function(angular) {
    angular
        .module('webformz.websocket', ["ngWebSocket"])
        .factory('WebSocketService', WebSocketService);

    WebSocketService.$inject = ['$websocket'];
    function WebSocketService($websocket) {
        var service = {};

        service.socket = $websocket("ws://localhost:8080", {reconnectIfNotNormalClose: true})
        service.subscribe = Subscribe
        service.unSubscribe = Unsubscribe

        return service;

        function Subscribe(channel, cb) {
            var r = channel
            if (typeof(channel) == "string") r = new RegExp(channel)

            service.socket.onMessage(function(message) {
                var json = JSON.parse(message.data)
                if (json.channel.match(r)) cb(json.data)
            })

            return service.socket.onMessageCallbacks.last()
        }

        function Unsubscribe(subscriber) {
          var index = service.socket.onMessageCallbacks.indexOf(subscriber)
          service.socket.onMessageCallbacks.splice(index, 1)
        }
    }
})

