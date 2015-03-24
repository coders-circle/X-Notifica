<div class="container">
    <div class="row">
        <div class="col-md-12 main">
            <h1 class="page-header">Add New Routine</h1>
            <form class="form-adduser" action="index.php?page=adminpage&amp;tab=routine" method="post" name="registration_form" role="form">
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
                    <div role="tabpanel" class="tab-pane fade in active" id="sun" aria-labelledBy="sunday-tab">
                        <div id = "day_routine">
                            <div id = "period" style = "visibility:hidden; ">
                                <div class="row">
                                    <div class="col-md-5">
                                        <input type="time" name="start"/>
                                        to
                                        <input type="time" name="end"/>
                                    </div>
                                    <div class="col-md-7">
                                        <input type="text"/>
                                        <input type="text"/>
                                    </div>
                                </div>
                            </div>

                        </div>
                        <br/>
                        <br/>
                        <button type="button" class="btn btn-default btn-lg" onClick="AddRow();">
                            <span class="glyphicon glyphicon-plus" aria-hidden="true"></span> Add a period
                        </button>
                    </div>
                    <div role="tabpanel" class="tab-pane fade in" id="mon" aria-labelledBy="monday-tab">
                        Monday
                    </div>
                    <div role="tabpanel" class="tab-pane fade in" id="tue" aria-labelledBy="tuesday-tab">
                        Tuesday
                    </div>
                    <div role="tabpanel" class="tab-pane fade in" id="wed" aria-labelledBy="wednesday-tab">
                        Wednesday
                    </div>
                    <div role="tabpanel" class="tab-pane fade in" id="thu" aria-labelledBy="thursday-tab">
                        Thursday
                    </div>
                    <div role="tabpanel" class="tab-pane fade in" id="fri" aria-labelledBy="friday-tab">
                        Friday
                    </div>
                    <div role="tabpanel" class="tab-pane fade in" id="sat" aria-labelledBy="saturday-tab">
                        Saturday
                    </div>
                </div>
            </form>
        </div>
    </div>
</div>
