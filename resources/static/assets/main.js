if( /Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i.test(navigator.userAgent) ) {
 // some code..
}else{
  window.onscroll = function() {myFunction()};


  var navbar = document.getElementById("navbar");
  var sticky = navbar.offsetTop;


  function myFunction() {
    if (window.pageYOffset >= sticky) {
      navbar.classList.add("sticky");
    } else {
      navbar.classList.remove("sticky");
    }
  }
}
//Now include js files

/*
      var images = [], x = 0;
          images[0] = "assets/pic1.jpg";
          images[1] = "assets/pic2.jpg";
          images[2] = "assets/pic3.jpg";

      
      function displayNextImage() {
              x++;
              if(x>=images.length){
                x = 0;
              }
              document.getElementById("img").style.backgroundImage = "url('"+images[x]+"')";
              console.log("change" + x);
      }

      function startTimer() {
              setInterval(displayNextImage, 5000);
      }
*/