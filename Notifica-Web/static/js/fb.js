var fb_publish_permission = false;
function fb_actualshare() {
    url = "/" + $("#fb_group_selector").find(":selected").attr("value") + "/feed";
    FB.api(url, "POST",
        {
            "message": document.getElementById("fbTextArea").value
        },
        function (response) {
            if (response && !response.error)
                alert("Posted successfully");
        });
}

function fb_share() {
    if (fb_publish_permission)
        fb_actualshare();
    else {
        FB.login(function(response){
            if (response.authResponse) {
                fb_publish_permission = true;
                fb_actualshare();
            } 
            else
                $("#fb_share_modal").modal('hide');
        }, {scope: 'publish_actions'});
    }
}

function fb_logged_in() {
    url = "/me/groups";
    FB.api(url, function(res) {
        if (res && !res.error) {
            $("#fb_group_selector").empty();
            for (var i=0; i<res.data.length; i++) {
                key = res.data[i].id;
                value = res.data[i].name;
                $("#fb_group_selector").append($("<option></option").attr("value",key).text(value));
            }
        }
    });
}

function fb_share_open(type, summary, posted_by, details, finaldate, subject, groups) {
    var text = type;
    if (subject != "")
        text += " of " + subject;
    text += "\n" + summary;
    if (posted_by != "")
        text += "\nPosted by: " + posted_by;
    if (finaldate != "None")
        text += "\nDate: " + finaldate;
    if (groups != "" && group != "None")
        text += "\nFor group: " + groups;
    text += "\n\n" + details;
    document.getElementById("fbTextArea").value = text;

    FB.getLoginStatus(function(response) {
        if (response.status === 'connected') {
            fb_logged_in();
        }
        else {
            FB.login(function(response){
                fb_logged_in();
            }, {scope: 'user_groups'});
        }
    });
}

