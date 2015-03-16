function formhash(form, password) {
	if(password.value.length == 0) return;
    var p = document.createElement("input");
    form.appendChild(p);
    p.name = "psss";
    p.type = "hidden";
    p.value = hex_sha512(password.value);
	var len = password.value.length;
	password.value = "";
	for(var i = 0; i < len; i++) {password.value += "-";}
}

function regformhash(form, typ, uid, password, conf) {
	if(typ.selectedIndex == 0){
		alert('Please select a user type first');
		form.UserCat.focus();
		return false;
	}
	if(typ.selectedIndex == 3){
		if(uid.value == ''){
			alert('You need to provide a username');
			form.un.focus();
			return false;
		}
		re = /^\w+$/;
		if(!re.test(form.un.value)) {
			alert("Username must contain only letters, numbers and underscores. Please try again");
			form.un.focus();
			return false;
		}
		form.submit();
		return true;
	}
    if (uid.value == '' || password.value == ''  || conf.value == '' ) {
        alert('You must provide all the requested details. Please try again');
        return false;
    }
    re = /^\w+$/;
    if(!re.test(form.un.value)) {
        alert("Username must contain only letters, numbers and underscores. Please try again");
        form.un.focus();
        return false;
    }
    if (pwd.value.length < 6) {
        alert('Passwords must be at least 6 characters long.  Please try again');
        form.pwd.focus();
        return false;
    }
    var re = /(?=.*\d)(?=.*[a-z])(?=.*[A-Z]).{6,}/;
    if (!re.test(password.value)) {
        alert('Passwords must contain at least one number, one lowercase and one uppercase letter.  Please try again');
        return false;
    }
    if (password.value != conf.value) {
        alert('Your password and confirmation do not match. Please try again');
        form.pwd.focus();
        return false;
    }
	var p = document.createElement("input");
    form.appendChild(p);
    p.name = "p";
    p.type = "hidden";
    p.value = hex_sha512(password.value);
	var len = password.value.length;
	password.value = "";
	for(var i = 0; i < len; i++) {form.pwd.value += "-";}
    var len = conf.value.length;
    conf.value = "";
	for(var i = 0; i < len; i++) {form.confpwd.value += "-";}
    form.submit();
    return true;
}
