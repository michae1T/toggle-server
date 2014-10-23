package com.michaelt.toggleserver.resources

import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType
import com.michaelt.toggleserver.helper.JQuery

@Path("/")
@Produces(Array(MediaType.TEXT_HTML))
class RootHandler {

lazy val pageData = """
<html>
<head>
<title>toggler</title>

<script>%s</script>

<script>

function getState(toggle, success) {
  $.get("/toggle/"+toggle, success)
}

function setState(toggle, data) {
  $.ajax({'url':'/toggle/'+toggle, 'data' : data, 'type' : 'PUT'});
}

function updateState(toggle, $contents, $toggle) {
  getState(toggle, function (data) {
    $contents.text(data)
    if (data == 'on') {
      $toggle.text('off');
    } else {
      $toggle.text('on');
    }
  });
}

function updateIp($elem) {
  $.getJSON("http://smart-ip.net/geoip-json?callback=?", function(data){
    $elem.text(data.host + ' - '+ data.countryName);
  });
}

</script>

<style type="text/css">
body {
  font-family: sans-serif;
  text-align: center;
  font-size: 22px;
}

#vpn-toggle {
  height: 46px;
  margin: 25px;
  width: 88px;
}
</style>

</head>

<body>
<br/>&nbsp<br/>
<h1>VPN Toggler</h1>
<span id="vpn-contents"></span><button id="vpn-toggle"></button>
<br>
<i>ip: <span id="ip-addr">...</span></i>
<script>
$(document).ready(function () {
  var $contents = $('#vpn-contents');
  var $toggle = $('#vpn-toggle');
  var $ip = $('#ip-addr');
  var toggle = 'vpn-pi'

  updateState(toggle, $contents, $toggle);

  $toggle.click(function (d) {
    setState(toggle, $toggle.text());
    updateState(toggle, $contents, $toggle);
  });

  window.setInterval(function () {
    updateIp($ip);
  }, 5000);
});
</script>

</body>
</html>
""".format(JQuery.source.getOrElse("// could not find jquery"))

  @GET
  def index = {
    pageData
  }

}

