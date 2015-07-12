$(function(){
  var hash = window.location.hash;
  hash && $('ul.nav a[href="' + hash + '"]').tab('show');

  $('.navbar-right a').click(function (e) {
    $(this).tab('show');
    var scrollmem = $('body').scrollTop();
    window.location.hash = this.hash;
    $('html,body').scrollTop(scrollmem);
  });
});


$("#pinned-assignment").click(function(){
    $("#date-assignment").attr('disabled', this.checked)
    $("#date-assignment").attr('required', !this.checked)
});

$("#pinned-notice").click(function(){
    $("#date-notice").attr('disabled', this.checked)
    $("#date-notice").attr('required', !this.checked)
});
