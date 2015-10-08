require.config({
  baseUrl: "/assets/webformz",
  shim: {
    angular: {
      exports: "angular"
    },
    "angular-route": {
      deps: [
        "angular"
      ]
    },
    "angular-websocket": {
      deps: [
        "angular"
      ]
    }
  },
  paths: {
    angular: "/assets/angular",
    "angular-loader": "/assets/angular-loader",
    "angular-mocks": "/assets/angular-mocks",
    "angular-route": "/assets/angular-route",
    bootstrap: "/assets/dist/bootstrap",
    "html5-boilerplate": "/assets/gulpfile",
    jquery: "/assets/jquery",
    "js-cookie": "/assets/webformz/js-cookie",
    requirejs: "/assets/require",
    "angular-websocket": "/assets/angular-websocket/dist/angular-websocket.min"
  },
  packages: [

  ]
});

require(["/assets/webformz/main.js"])
