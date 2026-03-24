using MadridTransporte.Api.Data.Entities;
using Microsoft.EntityFrameworkCore;

namespace MadridTransporte.Api.Data;

public class AppDbContext(DbContextOptions<AppDbContext> options) : DbContext(options)
{
    public DbSet<Stop> Stops => Set<Stop>();
    public DbSet<TransitRoute> Routes => Set<TransitRoute>();
    public DbSet<Itinerary> Itineraries => Set<Itinerary>();
    public DbSet<StopOrder> StopOrders => Set<StopOrder>();
    public DbSet<Calendar> Calendars => Set<Calendar>();
    public DbSet<Shape> Shapes => Set<Shape>();
    public DbSet<StopInfo> StopInfos => Set<StopInfo>();

    protected override void OnModelCreating(ModelBuilder modelBuilder)
    {
        modelBuilder.Entity<Stop>(entity =>
        {
            entity.HasKey(e => e.FullStopCode);
            entity.HasIndex(e => e.CodMode);
        });

        modelBuilder.Entity<TransitRoute>(entity =>
        {
            entity.HasKey(e => e.FullLineCode);
            entity.HasIndex(e => e.CodMode);
        });

        modelBuilder.Entity<Itinerary>(entity =>
        {
            entity.HasKey(e => e.TripId);
            entity.HasIndex(e => e.ItineraryCode);
            entity.HasIndex(e => e.FullLineCode);
            entity.HasIndex(e => e.ServiceId);
        });

        modelBuilder.Entity<StopOrder>(entity =>
        {
            entity.HasKey(e => new { e.TripId, e.Order });
            entity.HasIndex(e => e.FullStopCode);
        });

        modelBuilder.Entity<Calendar>(entity =>
        {
            entity.HasIndex(e => e.ServiceId);
        });

        modelBuilder.Entity<Shape>(entity =>
        {
            entity.HasKey(e => new { e.ItineraryId, e.Sequence });
        });

        modelBuilder.Entity<StopInfo>(entity =>
        {
            entity.HasKey(e => new { e.IdEstacion, e.CodigoEmpresa });
        });
    }
}
