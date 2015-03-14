<div class="container">
    <div class="row">
        <div class="col-md-12 main">
            <div class="col-md-7">
                <h1 class="page-header">Student List</h1>
                <p> The following table contains students from the selected faculty. And some other instruction goes here </p>

                <table class="table">
    				<tr>
    					<th>S.N.</th>
    					<th>Name</th>
    					<th>Roll</th>
    				</tr>
                    <?php
                        $user = $GLOBALS['g_user'];
                        $result = $user->GetStudents();
                        $count = 1;
                        while ($row = $result->fetch_assoc()) {
                            echo '<tr><td>'.$count++.'</td><td>'.$row['name'].'</td><td>'.$row['year'].'-BCT-'.$row['roll'].'</td></tr>';
                        }
                    ?>
                </table>
            </div>
            <div class="col-md-5">
                <h1 class="page-header">Quick Add</h1>
                <form class="form-adduser" action="index.php?page=adminpage&amp;tab=students" method="post" name="registration_form" role="form">
    	            <input type='text' class="form-control" placeholder="Name" name='studentname' id='studentname' required >
                    <input type='text' class="form-control" placeholder="Faculty" name='faculty' id='faculty' required>
        			<input type="number" class="form-control" placeholder="Year (Batch)" name="batch" id="batch" required>
                    <input type="number" class="form-control" placeholder="Roll No." name="roll" id="roll" required>

                    <input type="submit" class="btn btn-lg btn-primary btn-block" value="Add Student"/>
                </form>
            </div>
        </div>

    </div>
</div>
