var open = null;
var onglet = 'doc';
$(document).on('click','button',function() {
    
    if($(this).attr('class') == "btn btn-default changeToDoc")
    {
        $(this).parent('div').find('.example').hide();
        $(this).parent('div').find('.doc').show();
    }
    if($(this).attr('class') == "btn btn-default changeToExample")
    {
        $(this).parent('div').find('.doc').hide();
        $(this).parent('div').find('.example').show();
    }
    
    if($(this).attr('class') == "btn btn-default col-xs-12 displayDocumentation")
    {
        if(open != null){
            //Fermeture de l'ancienne fenetre
            open.parent('div').find('.documentation').toggle(1000);
            open.parent('div').find(".url").html('');
            open.parent('div').find(".data").html('');
        }

        if(open == null || $(this).parent('div').attr('id') != open.parent('div').attr('id')){
            $(this).parent('div').find('.documentation').toggle(1000);
            open = $(this);
            }
        else{
            open = null;
        }
    }
    
    if($(this).attr("id") == "trygetFriendRequest"){
        
        $("#getFriendRequest").find(".url").html('<h4 class="text-center">Request Url</h4>' +
                '<div class="alert alert-warning">GET /findme/api/friendrequest/v1' +
                '?caller='+$("#getFriendRequest").find("#caller").val()+
                '&receiver='+$("#getFriendRequest").find("#receiver").val()+'</div>');
        $("#getFriendRequest").find(".data").html('<h4 class="text-center">Data</h4>' +
                '<div class="alert alert-warning">None</div>');
    }
    if($(this).attr("id") == "tryputFriendRequest"){
        $("#putFriendRequest").find(".url").html('<h4 class="text-center">Request Url</h4>' +
                '<div class="alert alert-warning">PUT /findme/api/friendrequest/v1</div>');
        $("#putFriendRequest").find(".data").html('<h4 class="text-center">Data</h4>' +
                '<div class="alert alert-warning">{"caller":"'+$("#putFriendRequest").find("#caller").val()+'","receiver":"'+$("#putFriendRequest").find("#receiver").val()+'"}</div>')
    }
    if($(this).attr("id") == "trypostFriendRequest"){
        $("#postFriendRequest").find(".url").html('<h4 class="text-center">Request Url</h4>' +
                '<div class="alert alert-warning">POST /findme/api/friendrequest/v1</div>');
        $("#postFriendRequest").find(".data").html('<h4 class="text-center">Data</h4>' +
                '<div class="alert alert-warning">{"caller":"'+$("#postFriendRequest").find("#caller").val()+'","receiver":"'+$("#postFriendRequest").find("#receiver").val()+'"}</div>')
    }
    if($(this).attr("id") == "trydeleteFriendRequest"){
        $("#deleteFriendRequest").find(".url").html('<h4 class="text-center">Request Url</h4>' +
                '<div class="alert alert-warning">DELETE /findme/api/friendrequest/v1</div>');
        $("#deleteFriendRequest").find(".data").html('<h4 class="text-center">Data</h4>' +
                '<div class="alert alert-warning">{"caller":"'+$("#deleteFriendRequest").find("#caller").val()+'","receiver":"'+$("#deleteFriendRequest").find("#receiver").val()+'"}</div>')
    }

    if($(this).attr("id") == "trygetUser") {
        $("#getUser").find(".url").html('<h4 class="text-center">Request Url</h4>' +
                '<div class="alert alert-warning">GET /findme/api/user/v1/' + $("#getUser").find("#pseudo").val()+'</div>');
        $("#getUser").find(".data").html('<h4 class="text-center">Data</h4>' +
                '')
    }
    if($(this).attr("id") == "trydeleteUser") {
        $("#deleteUser").find(".url").html('<h4 class="text-center">Request Url</h4>' +
                '<div class="alert alert-warning">GET /findme/api/user/v1/' +$("#deleteUser").find("#pseudo").val()+'</div>');
        $("#deleteUser").find(".data").html('<h4 class="text-center">Data</h4>' +
                '')
    }
    if($(this).attr("id") == "tryputUser"){
        $("#putUser").find(".url").html('<h4 class="text-center">Request Url</h4>' +
                '<div class="alert alert-warning">PUT /findme/api/user/v1</div>');
        $("#putUser").find(".data").html('<h4 class="text-center">Data</h4>' +
                '<div class="alert alert-warning">{ "pseudo" : "' + $("#putUser").find("#pseudo").val() +'", ' +
                '"password" : "' + $("#putUser").find("#password").val() +'", ' +
                '"latitude" : "' + $("#putUser").find("#latitude").val() +'", ' +
                '"longitude" : "' + $("#putUser").find("#longitude").val() +'"} </div>' );
    }

    if($(this).attr("id") == "trypostUser"){
        $("#postUser").find(".url").html('<h4 class="text-center">Request Url</h4>' +
                '<div class="alert alert-warning">POST /findme/api/user/v1</div>');
        $("#postUser").find(".data").html('<h4 class="text-center">Data</h4>' +
                '<div class="alert alert-warning">{ "_id" : "' + $("#postUser").find("#id").val() +'", ' +
                '"pseudo" : "' + $("#postUser").find("#pseudo").val() +'", ' +
                '"password" : "' + $("#postUser").find("#password").val() +'", ' +
                '"latitude" : "' + $("#postUser").find("#latitude").val() +'", ' +
                '"longitude" : "' + $("#postUser").find("#longitude").val() +'"} </div>' );
    }

    if($(this).attr("id") == "trypostLogin"){
        $("#postLogin").find(".url").html('<h4 class="text-center">Request Url</h4>' +
                '<div class="alert alert-warning">POST /findme/api/user/v1/login</div>');
        $("#postLogin").find(".data").html('<h4 class="text-center">Data</h4>' +
                '<div class="alert alert-warning">{ "pseudo" : "' + $("#postLogin").find("#pseudo").val() +'", ' +
                '"password" : "' + $("#postLogin").find("#password").val() +'" } </div>' );
    }

});