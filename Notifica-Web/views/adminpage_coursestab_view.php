<div class="container">
    <div class="row">
        <div class="col-md-12 main">
            <div class="col-md-7">
                <h1 class="page-header">Course List</h1>
                <p> The following table contains courses from the selected faculty. And some other instruction goes here </p>

                <table class="table">
                    <tr>
                        <th>S.N.</th>
                        <th>Subject Name</th>
                        <th>Subject Code</th>
                    </tr>
                    <?php
                        $user = $GLOBALS['g_user'];
                        $result = $user->GetCourses();
                        $count = 1;
                        while ($row = $result->fetch_assoc()) {
                            echo '<tr><td>'.$count++.'</td><td>'.$row['name'].'</td><td>'.$row['code'].'</td></tr>';
                        }
                    ?>
                </table>
            </div>
            <div class="col-md-5">
                <h1 class="page-header">Quick Add</h1>
                <form class="form-adduser" action="index.php?page=adminpage&amp;tab=courses" method="post" name="registration_form" role="form">
                    <input type='text' class="form-control" placeholder="Subject Name" name="subjectname" id="subjectname" required>
                    <input type='text' class='form-control' placeholder="Subject Code" name="subjectcode" id="subjectcode" required>
                    <select class="form-control" placeholder="Select a faculty" name="faculty" id ="faculty" required>
                        <?php
                            $user = $GLOBALS['g_user'];
                            $result = $user->GetFaculties();
                            while($row = $result->fetch_assoc()){
                                echo '<option value = '.$row["id"].'">'.$row["name"].'</option>';
                            }
                        ?>
                    </select>
                    <input type="submit" class="btn btn-lg btn-primary btn-block" value="Add Course"/>
                </form>
            </div>
        </div>

    </div>
</div>
