YUI().use("core", "get", "overlay", "node", "loader", "console", function(Y) {
    Y.useBrowserConsole = true;
    (new Y.Console()).render();
    Y.log("Starting Main User Settings page", "info");
    
    // Adding event handler to the "Add account" button
    Y.get("#addTwitterAccount").on("click", function(e) {
    
    });
    
});



