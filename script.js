var main = function() {
  // Hides all paragraphs
  $("p").hide();

  $("h3").click(function() {
    // Toggles the paragraph under the header that is clicked. .slideToggle(200) can be changed to .slideDown(200) to make sure one paragraph is shown at all times.
    $("h3").removeClass("active");
    $("p").removeClass("active");
    $(this).addClass("active");
    $(this).next("p").addClass("active");
    $(this).next("p").slideToggle(200);
    // Makes other pararaphes that is not under the current clicked heading dissapear
    $(this).siblings().next("p").slideUp(200);
  });
}

$(document).ready(main);
