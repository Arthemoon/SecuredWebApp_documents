
$( document ).ready(function() {
  if(sessionStorage.access_token){
      window.location.replace("index.html");
  }
});

$(".form-login").on("submit", function(){
    var email = $("#login-email").val();
    var password = $("#login-password").val();
    
    if(email !== "" && email.length > 3 && password.length > 3 && password !== ""){
        authenticateUser(email, password);
    } 
    return false;
  });


  function authenticateUser(email, password) {
   $.ajax({
        url: "http://localhost:8085/oauth/token", 
        type: 'POST',
        dataType: 'json',
        headers: {"Authorization": "Basic bXktY2xpZW50Og==", "Content-Type": "application/x-www-form-urlencoded"},
        data: {
            "password": password,
            "username": email,
            "grant_type": "password" 
          },
        success: function(result) {
            sessionStorage.access_token = result.access_token;
            sessionStorage.refresh_token = result.refresh_token;

            window.location.replace("index.html");
        },
        error(xhr, textStatus, errorThrown){
        var object = JSON.parse(xhr.responseText);
        alert(object.error_description);
        $("#psw-error").show();
        $("#psw-error").text(object.error_description);
    }
    });
}


