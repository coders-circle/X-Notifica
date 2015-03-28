<div class="container">
    <div class="row">
        <div class="col-md-12 main">
            <h1 class="page-header">Add New Routine</h1>
            <form class="form-entry" action="index.php?page=adminpage&amp;tab=routine" method="post" name="registration_form" role="form">
                <div class="row" >
                    <div class="col-md-4">
                        <input type='number' class="form-control" placeholder="Batch" name="batch" id="batch"  required>
                    </div>
                    <div class="col-md-offset-4 col-md-4">
                        <input type='text' class="form-control" placeholder="Group" name="group" id="group"  required>
                    </div>
                </div>
                <br/>
                <ul class="nav nav-tabs " id="days-tab"  role="tablist">
                    <li role="presentation" class="active"><a href="#sun" id="sunday-tab" role="tab" data-toggle="tab" aria-controls="sunday" aria-expanded="true">Sunday</a></li>
                    <li role="presentation"><a href="#mon" role="tab" id="monday-tab" data-toggle="tab" aria-controls="monday">Monday</a></li>
                    <li role="presentation"><a href="#tue" role="tab" id="tuesday-tab" data-toggle="tab" aria-controls="tuesday">Tuesday</a></li>
                    <li role="presentation"><a href="#wed" role="tab" id="wednesday-tab" data-toggle="tab" aria-controls="wednesday">Wednesday</a></li>
                    <li role="presentation"><a href="#thu" role="tab" id="thursday-tab" data-toggle="tab" aria-controls="thursday">Thursday</a></li>
                    <li role="presentation"><a href="#fri" role="tab" id="friday-tab" data-toggle="tab" aria-controls="friday">Friday</a></li>
                    <li role="presentation"><a href="#sat" role="tab" id="saturday-tab" data-toggle="tab" aria-controls="saturday">Saturday</a></li>
                </ul>

                <div class="row" style="margin-top:20px">
                    <div class="col-md-4">
                        <b> Time </b>
                    </div>
                    <div class="col-md-3">
                        <b> Subject </b>
                    </div>
                    <div class="col-md-3">
                        <b> Teacher </b>
                    </div>
                    <div class="col-md-2">
                        <b> Option </b>
                    </div>
                </div>
                <hr/>
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
                                <span class="glyphicon glyphicon-plus-sign" aria-hidden="true"></span> Add a period
                            </button></div>';
                            ++$count;
                        }
                    ?>
                </div>
                <div class="row">
                    <div class="col-md-offset-8 col-md-4">
                        <input type="submit" class="btn btn-lg btn-primary btn-block" value="Save Routine"/>
                    </div>
                </div>
            </form>
        </div>
    </div>
    <div id = "period" style = "visibility:hidden; ">
        <div class="row">
            <div class="col-md-4">
                <div class="row">
                    <div class="col-md-5">
                        <input class="form-control" type="time" name="start[]" />
                    </div>
                    <div class="col-md-2">
                        <div style="margin-top:30px;"><b>to</b></div>
                    </div>
                    <div class="col-md-5">
                        <input class="form-control" type="time" name="end[]" />
                    </div>
                </div>
            </div>
            <div class="col-md-3">

                <select class="form-control" name="subject[]">
                    <?php
                    $user = $GLOBALS['g_user'];
                    $result = $user->GetCourses();
                    while($row = $result->fetch_assoc()){
                        echo '<option value = '.$row["id"].'>'.$row["name"].'</option>';
                    }
                    ?>
                </select>
            </div>
            <div class="col-md-3">

                <select class="form-control" name="teacher[]">
                    <?php
                    $user = $GLOBALS['g_user'];
                    $result = $user->GetTeachers();
                    while($row = $result->fetch_assoc()){
                        echo '<option value = '.$row["id"].'>'.$row["name"].'</option>';
                    }
                    ?>
                </select>
            </div>
            <div class="col-md-2">
                <button class="form-control" type="button" class="btn btn-default btn-lg">
                    <span class="glyphicon glyphicon-remove-sign" aria-hidden="true"></span> Remove
                </button>
            </div>
        </div>
    </div>
</div>
