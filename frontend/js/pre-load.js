
    $("body").hide();

    checkTokenValidity("http://localhost:8085/oauth/valid");


   function checkTokenValidity(url){
    $.ajax({
        'url': url,
        'type': 'GET',
        'headers': {
          'Authorization': 'Bearer ' + sessionStorage.access_token
        },
        'success': function (result) {
          if(result == "OK"){
            $("body").removeClass("body-initial");
          } else {
            window.location.replace("login.html");
            sessionStorage.clear();
          }
        },  
        'error': function (xhr, textStatus, errorThrown) {
          if(xhr.status === 0){
            alert("Could not contact the server. Try it later please.");
            window.location.replace("login.html");
            sessionStorage.clear();
            return;
          }

          if(xhr.status === 401){
            refreshToken();
          }
        }
    });
  }

   const refreshToken = () => {
    $.ajax({
            'url': "https://docmanager.com:8443/" + "oauth/token",
            'type': 'POST',
            'content-Type': 'x-www-form-urlencoded',
            headers: {"Authorization": "Basic bXktY2xpZW50Og=="}, 
            data: {
              refresh_token: sessionStorage.refresh_token,
              grant_type: 'refresh_token'
            },
            'success': function (result) {
              sessionStorage.access_token = result.access_token;
              sessionStorage.refresh_token = result.refresh_token;
              $("body").removeClass("body-initial");
            },
            'error': function (XMLHttpRequest, textStatus, errorThrown) {
              sessionStorage.clear();
              window.location.replace("login.html");
            }
        }); 
      }
