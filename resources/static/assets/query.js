          var input = document.getElementById("id");
            input.addEventListener("keyup", function(event) {
              if (event.keyCode === 13) {
               event.preventDefault();
               document.getElementById("submit").click();
           }
          });
          document.getElementById('submit').onclick = function() {
            var temp = document.getElementById('id').value;
            if(temp == "" || temp > 999999 || temp < 100000){
              alert("please enter a valid id");
            }else{
              location.href = '/query/' + document.getElementById('id').value;
            }
          
          };