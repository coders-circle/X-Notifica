<div class="container">
    <div class="row">
        <div class="col-md-1"></div>
        <div class="login-icon col-md-3">
            <h3>Welcome to </br><strong><?php echo $GLOBALS['g_appName'];?> </strong></h3>
        </div>
        <div class = "col-md-5">
            <form class="form-login" action="index.php?page=loginpage" method="post" role="form">
                <h2 class="form-login-heading">Log in</h2>
                <input type="text"  name="un" class="form-control"
                    placeholder="Username" required autofocus>
                <input type="password" name="pwd" class="form-control"
                    placeholder="Password" required>
                </br>
                <button class="btn btn-lg btn-primary btn-block"
                    type="submit" onclick="formhash(this.form, this.form.pwd);">
                    Log in
                </button>
            </form>
        </div>
    </div>
</div>
