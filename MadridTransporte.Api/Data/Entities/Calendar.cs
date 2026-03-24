namespace MadridTransporte.Api.Data.Entities;

public class Calendar
{
    public int Id { get; set; }
    public required string ServiceId { get; set; }
    public bool Monday { get; set; }
    public bool Tuesday { get; set; }
    public bool Wednesday { get; set; }
    public bool Thursday { get; set; }
    public bool Friday { get; set; }
    public bool Saturday { get; set; }
    public bool Sunday { get; set; }
    public long StartDate { get; set; }
    public long EndDate { get; set; }
}
