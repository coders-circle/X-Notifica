<div class="container">
    <div class="row">
        <div class="col-md-12 main">
            <h1 class="page-header">Add New Routine</h1>
            <form class="form-adduser" action="index.php?page=adminpage&amp;tab=routine" method="post" name="registration_form" role="form">
                <input type='number' placeholder="Batch" name="batch" id="batch" required>
                <select placeholder="Select a faculty" name="faculty" id ="faculty" required>
                    <?php
                        $user = $GLOBALS['g_user'];
                        $result = $user->GetFaculties();
                        while($row = $result->fetch_assoc()){
                            echo '<option value = '.$row["id"].'">'.$row["name"].'</option>';
                        }
                    ?>
                </select>
                <input type='text' placeholder="Group" name="group" id="group" required>
                <ul class="nav nav-tabs " id="days-tab"  role="tablist">
                    <li role="presentation" class="active"><a href="#sun" id="sunday-tab" role="tab" data-toggle="tab" aria-controls="sunday" aria-expanded="true">Sunday</a></li>
                    <li role="presentation"><a href="#mon" role="tab" id="monday-tab" data-toggle="tab" aria-controls="monday">Monday</a></li>
                    <li role="presentation"><a href="#tue" role="tab" id="tuesday-tab" data-toggle="tab" aria-controls="tuesday">Tuesday</a></li>
                    <li role="presentation"><a href="#wed" role="tab" id="wednesday-tab" data-toggle="tab" aria-controls="wednesday">Wednesday</a></li>
                    <li role="presentation"><a href="#thu" role="tab" id="thursday-tab" data-toggle="tab" aria-controls="thursday">Thursday</a></li>
                    <li role="presentation"><a href="#fri" role="tab" id="friday-tab" data-toggle="tab" aria-controls="friday">Friday</a></li>
                    <li role="presentation"><a href="#sat" role="tab" id="saturday-tab" data-toggle="tab" aria-controls="saturday">Saturday</a></li>
                </ul>

                <div id="days-tab-contents" class="tab-content">
                    <?php
                        $divlabels = array("sunday-tab", "monday-tab", "tuesday-tab", "wednesday-tab", "thursday-tab", "friday-tab", "saturday-tab");
                        $divids = array("sun", "mon", "tue", "wed", "thu", "fri", "sat");
                        $innerdivids = array("sun_routine", "mon_routine", "tue_routine", "wed_routine", "thu_routine", "fri_routine", "sat_routine");
                        $count = 0;
                        while($count < 7){
                            if($count == 0) echo '<div role="tabpanel" class="tab-pane fade in active" id="'.$divids[$count].'" aria-labelledBy="'.$divlabels[$count].'">';
                            else echo '<div role="tabpanel" class="tab-pane fade in" id="'.$divids[$count].'" aria-labelledBy="'.$divlabels[$count].'">';
                            echo '<br/>';
                            echo '<div id = "'.$innerdivids[$count].'">';
                            echo '</div>';
                            echo '<button type="button" class="btn btn-default btn-lg" onClick=\'AddRow("#'.$innerdivids[$count].'");\'>
                                <span class="glyphicon glyphicon-plus" aria-hidden="true"></span> Add a period
                            </button></div>';
                            ++$count;
                        }
                    ?>
                </div>
                <input type="submit" class="btn btn-lg btn-primary btn-block" value="Add Routine"/>
            </form>
        </div>
    </div>
    <div id = "period" style = "visibility:hidden; ">
        <div class="row">
            <div class="col-md-4">
                <b> Time </b>
                <input type="time" name="start[]" style="width:35%;"/>
                to
                <input type="time" name="end[]" style="width:35%;"/>
            </div>
            <div class="col-md-4">
                <b> Subject </b>
                <select name="subject[]" style="width:70%;">
                    <?php
                    $user = $GLOBALS['g_user'];
                    $result = $user->GetCourses();
                    while($row = $result->fetch_assoc()){
                        echo '<option value = '.$row["id"].'>'.$row["name"].'</option>';
                    }
                    ?>
                </select>
            </div>
            <div class="col-md-4">
                <b> Teacher </b>
                <select name="teacher[]" style="width:70%;">
                    <?php
                    $user = $GLOBALS['g_user'];
                    $result = $user->GetTeachers();
                    while($row = $result->fetch_assoc()){
                        echo '<option value = '.$row["id"].'>'.$row["name"].'</option>';
                    }
                    ?>
                </select>
            </div>
        </div>
    </div>
</div>
