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


function openModal(modal_name) {
        $(modal_name).modal('show');
    }

function RescheduleClass(classID,  start_time, subject){
    var p_time_old = document.getElementById('modal-reschedule-class-label-time-old');
    var p_subject = document.getElementById('modal-reschedule-class-label-subject');
    var input_new_time = document.getElementById('modal-reschedule-class-input-time');
    input_new_time.placeholder = "New time";
    p_subject.innerHTML = subject;
    p_time_old.innerHTML = 'Regular time - ' + start_time;
    openModal('#modal-reschedule-class');
    //prompt("New Start Time:", "");
}

function CancelClass(classID){
    confirm("Are you sure to remove this class?");
}
