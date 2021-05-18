window.addEventListener( "load", function () {
  function getResult() {
    let httpRequest = new XMLHttpRequest();
    const FD = new FormData(form);

    if (!httpRequest) {
      console.log('Unable to create XMLHTTP instance');
      return false;
    }
    httpRequest.open('POST', "startCrawler");

    httpRequest.send(FD);
    httpRequest.onreadystatechange = function () {
      if (httpRequest.readyState === XMLHttpRequest.DONE) {
        if (httpRequest.status === 200) {
          document.getElementById("result").innerText = httpRequest.response;
        } else {
          console.log('Something went wrong..!!');
        }
      }
    }
  }
  const form = document.getElementById("crawlerForm");
  form.addEventListener( "submit", function ( event ) {
    event.preventDefault();

    getResult();
  } );
});

