function like(btn, entityType, entityId, entityUserId, postId){
       $.post(
       CONTEXT_PATH + "/like",
       {"entityType":entityType,"entityId":entityId, "entityUserId":entityUserId,"postId":postId},
       function(data){
        data = $.parseJSON(data);
        if(data.code == 0){
        console.log(data);
            $(btn).children("i").text(data.likeCount);
            $(btn).children("b").text(data.likeStatus?"已赞":"赞");
        }else{
            alert(data.msg);
        }
       }
       );
}