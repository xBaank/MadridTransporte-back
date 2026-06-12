using MadridTransporte.Api.Utils;
using Shouldly;

namespace MadridTransporte.Tests;

// Pure-logic unit tests for line-code parsing. These guard the EMT regression where a bare
// line code (the EMT URL form, e.g. "/lines/emt/881/locations/1") with no route row in the DB
// fell through to GetSimpleLineCodeFromLineCode and threw IndexOutOfRangeException -> HTTP 500.
public class CodeUtilsTests
{
    // Full line codes ("codMode__line___") keep their existing behavior.
    [Test]
    [Arguments("8__450___", "450")]
    [Arguments("4__12___", "12")]
    [Arguments("5__4_A__", "4_A")]
    [Arguments("10__ML4___", "ML4")]
    [Arguments("9__2__065_", "2")]
    public async Task GetSimpleLineCodeFromLineCode_Parses_Full_Codes(string input, string expected)
    {
        CodeUtils.GetSimpleLineCodeFromLineCode(input).ShouldBe(expected);
        await Task.CompletedTask;
    }

    // EMT URLs use the bare line code (route_id), which has no "__" separator. It must be
    // returned unchanged instead of throwing.
    [Test]
    [Arguments("881")]
    [Arguments("144")]
    [Arguments("N26")]
    public async Task GetSimpleLineCodeFromLineCode_Returns_Bare_Code_Unchanged(string input)
    {
        Should.NotThrow(() => CodeUtils.GetSimpleLineCodeFromLineCode(input));
        CodeUtils.GetSimpleLineCodeFromLineCode(input).ShouldBe(input);
        await Task.CompletedTask;
    }
}
