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
            entity.HasIndex(e => e.FullStopCode).IsUnique();
            entity.HasIndex(e => e.CodMode);
        });

        modelBuilder.Entity<TransitRoute>(entity =>
        {
            entity.HasIndex(e => e.FullLineCode).IsUnique();
            entity.HasIndex(e => e.CodMode);
        });

        modelBuilder.Entity<Itinerary>(entity =>
        {
            entity.HasIndex(e => e.ItineraryCode);
            entity.HasIndex(e => e.FullLineCode);
            entity.HasIndex(e => e.TripId);
            entity.HasIndex(e => e.ServiceId);
        });

        modelBuilder.Entity<StopOrder>(entity =>
        {
            entity.HasIndex(e => e.FullStopCode);
            entity.HasIndex(e => e.TripId);
        });

        modelBuilder.Entity<Calendar>(entity =>
        {
            entity.HasIndex(e => e.ServiceId);
        });

        modelBuilder.Entity<Shape>(entity =>
        {
            entity.HasIndex(e => e.ItineraryId);
        });

        modelBuilder.Entity<StopInfo>(entity =>
        {
            entity.HasIndex(e => e.CodigoEmpresa);
            entity.HasIndex(e => e.IdEstacion);
        });
    }
}
