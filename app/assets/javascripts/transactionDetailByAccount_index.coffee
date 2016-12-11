row_id = location.pathname.split('/')[2]
totalDebit = 0
totalCredit = 0
$ ->
  $.get "/transactionDetailsByAccount/" + row_id, (rows) ->
    $.each rows, (index, row) ->
      transactionId = $("<td>").text row.transactionId
      accountId = $("<td>").text row.accountId
      debit = $("<td>").text row.debit
      credit = $("<td>").text row.credit
      links = $("<td>").html '<a href="/transactionDetail_update/' + row.id + '" class="btn btn-primary btn-sm">Edit</a>' + '<a href="/transactionDetail_remove/' + row.id + '" class="btn btn-danger btn-sm">Remove</a>' + '<a href="/transactionDetail_show/' + row.id + '" class="btn btn-info btn-sm">view</a>'
      $("#rows").append $("<tr>").append(transactionId).append(accountId).append(debit).append(credit).append(links)
      totalDebit += row.debit
      totalCredit += row.credit
      $("#totalDebit").html totalDebit
      $("#totalCredit").html totalCredit
