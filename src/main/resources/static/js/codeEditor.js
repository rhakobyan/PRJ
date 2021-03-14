$(function() {
    var javaCodeMirror = CodeMirror($('#codeEditor')[0], {
              value: problem,
              mode:  "text/x-java",
              lineNumbers: true,
              indentUnit: 4,
              theme: 'darcula'
    });

    $("#run").click(function() {
        var codeEditorValue = javaCodeMirror.getValue();
        $.post("/lessons/run",
        {
            code: codeEditorValue
        },
        function(result, status) {
            var span = "";
            if (result.type == "error") {
               span = "<span class='text-danger' style='white-space: pre-wrap;'>" + result.message + "</span>";
            }
            else if (result.type == "success") {
               span = "<span class='text-light' style='white-space: pre-wrap;'>" +result.message + "</span>";
            }
            $("#output").html(span);
        });
    });

    $("#hint").click(function() {
        var codeEditorValue = javaCodeMirror.getValue();
        $.post("/lessons/hint",
        {
            id: problemId,
            code: codeEditorValue
        },
        function(result, status) {
            $("#hintModal").modal("show");
            $("#hintParagraph").html(result);
        });
    });

});