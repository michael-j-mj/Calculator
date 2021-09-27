$(document).ready(function () {
  $("#calculater-form").submit((event) => {
    event.preventDefault();
    //set up the submission
    url = $("#calculater-form").attr("action");
    let input =
      "/" +
      "leftOperand=" +
      $("#left-Operand").val() +
      "&rightOperand=" +
      $("#right-Operand").val() +
      "&Operation=" +
      $('input[name="operation"]:checked').val();

    /* Send the data using post with element id*/
    let posting = $.getJSON(url + input, (data) => {
      $("#expression").text(data.Expression);
      $("#result").text(data.Result);
      if (data.Result.substring(0, 5) == "ERROR") {
        $("#result").css("color", "red");
      } else {
        $("#result").css("color", "black");
      }
    });
  });
});