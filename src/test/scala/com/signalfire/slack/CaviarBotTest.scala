package com.signalfire.slack

import org.scalatest.FunSuite

class CaviarBotTest extends FunSuite {

  test ("caviar page parser for open restaurants") {
    val html = """<li id="merchant-71" data-reactid=".1iurh6blo1s.1.5"><a href="https://www.trycaviar.com/san-francisco/freshroll-soma-71" data-reactid=".1iurh6blo1s.1.5.0"><div class="tile_image" style="background-image:url(https://img.trycaviar.com/RxbMiz19V5GLXma6NXXXMsisnfY=/789x315/https://s3.amazonaws.com/trycaviar.com/offers/71/2596.jpg);" data-reactid=".1iurh6blo1s.1.5.0.0"></div><h4 data-reactid=".1iurh6blo1s.1.5.0.1"><span data-reactid=".1iurh6blo1s.1.5.0.1.1">Freshroll SOMA</span></h4><p data-reactid=".1iurh6blo1s.1.5.0.2">Vietnamese Rolls &amp; Bowls.</p><p class="eta-wrapper" data-reactid=".1iurh6blo1s.1.5.0.3"><span data-reactid=".1iurh6blo1s.1.5.0.3.0"></span><span data-reactid=".1iurh6blo1s.1.5.0.3.1"></span></p></a></li>"""
    val restaurants = CaviarBot.parseCaviarHomepage(html)
    assert(restaurants.length == 1)
    val r = restaurants.head
    assert(r.name.equals("Freshroll SOMA"))
    assert(r.text.equals("Vietnamese Rolls & Bowls."))
    assert(r.image.equals("https://s3.amazonaws.com/trycaviar.com/offers/71/2596.jpg"))
  }

  test ("caviar page parser for closed retaurants") {
    val html = """<li id="merchant-27" data-reactid=".13wzu6jvpxc.1.3"><a href="https://www.trycaviar.com/san-francisco/american-grilled-cheese-kitchen--mission-27" data-reactid=".13wzu6jvpxc.1.3.0"><div class="tile_image" style="background-image:url(https://img.trycaviar.com/amy5TtBOFwOu8j4lH45rb4zPoX4=/789x315/https://s3.amazonaws.com/trycaviar.com/offers/27/1135.jpg);" data-reactid=".13wzu6jvpxc.1.3.0.0"><div class="merchant-tile_overlay actionable" data-reactid=".13wzu6jvpxc.1.3.0.0.0"><h4 data-reactid=".13wzu6jvpxc.1.3.0.0.0.0">Pre-Order for Later</h4></div></div><h4 data-reactid=".13wzu6jvpxc.1.3.0.1"><span data-reactid=".13wzu6jvpxc.1.3.0.1.1">American Grilled Cheese Kitchen - Mission</span></h4><p data-reactid=".13wzu6jvpxc.1.3.0.2">Gourmet Comfort.</p><p class="eta-wrapper" data-reactid=".13wzu6jvpxc.1.3.0.3"><span data-reactid=".13wzu6jvpxc.1.3.0.3.0"></span><span class="eta_opening-time" data-reactid=".13wzu6jvpxc.1.3.0.3.1">Wednesday at 7:00pm</span></p></a></li>"""
    val restaurants = CaviarBot.parseCaviarHomepage(html)
    assert(restaurants.length == 1)
    val r = restaurants.head
    assert(r.name.equals("American Grilled Cheese Kitchen - Mission"))
    assert(r.text.equals("Gourmet Comfort."))
    assert(r.image.equals("https://s3.amazonaws.com/trycaviar.com/offers/27/1135.jpg"))
  }
}
