var stuckCounter = 0;
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
            id: problemId,
            code: codeEditorValue
        },
        function(result, status) {
            var span = "";
            if (result.type == "error") {
                ++stuckCounter;
               span = "<span class='text-danger' style='white-space: pre-wrap;'>" + result.message + "</span>";
            }
            else if (result.type == "success") {
                span = "<div>"
               span += "<span class='text-light' style='white-space: pre-wrap;'>" +result.message + "</span>";
               if (result.solved == "true") {
                   $("#nextLesson").removeClass("btn-disabled btn-light");
                   $("#nextLesson").addClass("btn-warning");
                   span += "<p class='text-success'>Your solution is correct!</p>";
               } else {
                    ++stuckCounter;
                    span += "<p class='text-danger'>Your solution is incorrect!</p>";
               }
            }
            span += "</div>";
            $("#output").html(span);
        });

        showSolution();
    });

    $("#hint").click(function() {
        var codeEditorValue = javaCodeMirror.getValue();
        $.post("/lessons/hint",
        {
            id: problemId,
            code: codeEditorValue
        },
        function(result, status) {
            ++stuckCounter;
            $("#modal").modal("show");
            $("#modalTitle").html("Hint");
            $("#modalParagraph").html(result);
        });

        showSolution();
    });

    function showSolution() {
        if (stuckCounter >= 3 && solutionRequired) {
            $("#showSolution").show();
        }
    }

    $("#showSolution").click(function() {
        var codeEditorValue = javaCodeMirror.getValue();
         $.post("/lessons/solution",
        {
            id: problemId,
            code: codeEditorValue
        },
        function(result, status) {
            if (result != "") {
                $("#modal").modal("show");
                $("#modalTitle").html("Solution");
                var preCode = "<pre class='bg-dark text-white'><code>" + result + "</code></pre>";
                $("#modalParagraph").html(preCode);
            }
        });
    });

});